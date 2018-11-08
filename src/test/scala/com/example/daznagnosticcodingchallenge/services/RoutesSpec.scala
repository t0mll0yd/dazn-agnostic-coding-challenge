package com.example.daznagnosticcodingchallenge.services

import com.example.daznagnosticcodingchallenge.stores.Store
import com.twitter.finagle.http.{BasicAuth, Method, Request}
import com.twitter.util.{Await, Future}
import org.specs2.mutable.Specification
import org.json4s._
import org.json4s.native.JsonMethods._

// Returns a predefined set of streams, regardless of userId
case class StubbedStore(streams: Set[String] = Set()) extends Store {
  var getStreamCalledWith: Option[String] = None
  var addStreamCalledWith: Option[(String, String)] = None

  def getStreams(userId: String): Future[Set[String]] = {
    getStreamCalledWith = Some(userId)
    Future.value(streams)
  }

  def addStream(userId: String, streamId: String): Future[Unit] = {
    addStreamCalledWith = Some(userId, streamId)
    Future.Unit
  }
}

class RoutesSpec extends Specification {
  private val withAuthHeader = BasicAuth.client("username", "password")

  private def makeService(streams: Set[String] = Set()) = {
    val store = StubbedStore(streams)
    val service = Routes.makeService(store)

    (service, store)
  }

  "Routes" in {
    "GET '/' returns 404 NOT FOUND" in {
      val (service, store) = makeService()

      val response = Await.result {
        service(Request(Method.Get, "/"))
      }

      store.getStreamCalledWith must beNone
      store.addStreamCalledWith must beNone

      response.statusCode must beEqualTo(404)
    }

    "GET '/health-check' returns 200 OK" in {
      val (service, store) = makeService()

      val response = Await.result {
        service(Request(Method.Get, "/health-check"))
      }

      store.getStreamCalledWith must beNone
      store.addStreamCalledWith must beNone

      response.statusCode must beEqualTo(200)
      response.contentType must beSome("application/json")

      parse(response.contentString) must beEqualTo {
        parse("""
            { "message": "Hello from the Scala/Finagle based DAZN coding challenge service!" }
        """)
      }
    }

    "GET '/users/USER_ID/streams/STREAM_ID' returns 404 NOT FOUND" in {
      val (service, store) = makeService()

      val response = Await.result {
        service(Request(Method.Get, "/users/1/streams/1"))
      }

      store.getStreamCalledWith must beNone
      store.addStreamCalledWith must beNone

      response.statusCode must beEqualTo(404)
    }

    "POST '/users/USER_ID/streams/STREAM_ID' without basic auth returns 401 UNAUTHORIZED" in {
      val (service, store) = makeService()

      val response = Await.result {
        service(Request(Method.Post, "/users/1/streams/1"))
      }

      store.getStreamCalledWith must beNone
      store.addStreamCalledWith must beNone

      response.statusCode must beEqualTo(401)
    }

    "POST '/users/USER_ID/streams/STREAM_ID' returns 201 CREATED" in {
      val (service, store) = makeService()

      val response = Await.result {
        (withAuthHeader andThen service) (Request(Method.Post, "/users/user1/streams/stream1"))
      }

      store.getStreamCalledWith must beSome("user1")
      store.addStreamCalledWith must beSome("user1", "stream1")

      response.statusCode must beEqualTo(201)
    }

    "POST with already added STREAM_ID returns 200 OK" in {
      val (service, store) = makeService(Set("stream1"))

      val response = Await.result {
        (withAuthHeader andThen service) (Request(Method.Post, "/users/user1/streams/stream1"))
      }

      store.getStreamCalledWith must beSome("user1")
      store.addStreamCalledWith must beNone

      response.statusCode must beEqualTo(200)
    }

    "Fourth added STREAM_ID for a user returns 409 Conflict" in {
      val (service, store) = makeService(Set("stream1", "stream2", "stream3"))

      val response = Await.result {
        (withAuthHeader andThen service) (Request(Method.Post, "/users/user1/streams/stream4"))
      }

      store.getStreamCalledWith must beSome("user1")
      store.addStreamCalledWith must beNone

      response.statusCode must beEqualTo(409)
      response.contentType must beSome("application/json")

      parse(response.contentString) must beEqualTo {
        parse("""
            {
                "code": "streams.limit.reached",
                "message": "This user already has the maximum number of concurrent streams."
            }
        """)
      }
    }
  }
}
