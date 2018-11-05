package com.example.daznagnosticcodingchallenge

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http
import com.twitter.util.{Await, Future}

object Server extends App {
  val service = new Service[http.Request, http.Response] {
    def apply(req: http.Request): Future[http.Response] =
      Future.value {
        val response = http.Response()
        response.contentString = """{ "hello": "world" }"""
        response.contentType = "application/json"
        response
      }
  }
  val server = Http.serve(":8080", service)
  Await.ready(server)
}
