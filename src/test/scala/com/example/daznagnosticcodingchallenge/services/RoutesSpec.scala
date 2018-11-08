package com.example.daznagnosticcodingchallenge.services

import com.example.daznagnosticcodingchallenge.stores.Store
import com.twitter.finagle.http.{BasicAuth, Method, Request, Response}
import com.twitter.util.{Await, Future}
import org.specs2.mutable.Specification

// Returns a predefined set of streams, regardless of userId
case class StubbedStore(streams: Set[String] = Set()) extends Store {
  def getStreams(userId: String): Future[Set[String]] = Future.value(streams)

  def addStream(userId: String, streamId: String): Future[Unit] = Future.Unit
}

class RoutesSpec extends Specification {
  private val withAuthHeader = BasicAuth.client("username", "password")

  private val routeService = Routes.makeService(StubbedStore())

  "Routes" in {
    "GET '/' returns 404 NOT FOUND" in {
      val response = Await.result {
        routeService(Request(Method.Get, "/"))
      }

      response.statusCode must beEqualTo(404)
    }

    "GET '/health-check' returns 200 OK" in {
      val response = Await.result {
        routeService(Request(Method.Get, "/health-check"))
      }

      response.statusCode must beEqualTo(200)
    }

    "GET '/users/USER_ID/streams/STREAM_ID' returns 404 NOT FOUND" in {
      val response = Await.result {
        routeService(Request(Method.Get, "/users/1/streams/1"))
      }

      response.statusCode must beEqualTo(404)
    }

    "POST '/users/USER_ID/streams/STREAM_ID' without basic auth returns 401 UNAUTHORIZED" in {
      val response = Await.result {
        routeService(Request(Method.Post, "/users/1/streams/1"))
      }

      response.statusCode must beEqualTo(401)
    }

    "POST '/users/USER_ID/streams/STREAM_ID' returns 201 CREATED" in {
      val response = Await.result {
        (withAuthHeader andThen routeService)(Request(Method.Post, "/users/1/streams/1"))
      }

      response.statusCode must beEqualTo(201)
    }

    "POST with already added STREAM_ID returns 200 OK" in {
      val routeService = Routes.makeService {
        StubbedStore(streams = Set("1"))
      }

      val response = Await.result {
        (withAuthHeader andThen routeService)(Request(Method.Post, "/users/1/streams/1"))
      }

      response.statusCode must beEqualTo(200)
    }

    "Fourth added STREAM_ID for a user returns 409 Conflict" in {
      val routeService = Routes.makeService {
        StubbedStore(streams = Set("1", "2", "3"))
      }

      val response = Await.result {
        (withAuthHeader andThen routeService)(Request(Method.Post, "/users/1/streams/4"))
      }

      response.statusCode must beEqualTo(409)
    }
  }
}
