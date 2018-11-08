package com.example.daznagnosticcodingchallenge.services

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Service, http}
import com.twitter.util.Future
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

case class HealthCheckResponseBody(message: String)

object HealthCheckService extends Service[Request, Response] {
  implicit val formats = DefaultFormats

  def apply(request: Request): Future[Response] = Future.value {
    val response = http.Response(Status.Ok)

    response.contentType = "application/json"
    response.contentString = write(HealthCheckResponseBody(
      "Hello from the Scala/Finagle based DAZN coding challenge service!"
    ))

    response
  }
}
