package com.example.daznagnosticcodingchallenge.services

import com.twitter.finagle.http.{Request, Status, Response}
import com.twitter.finagle.{Service, http}
import com.twitter.util.Future

object HealthCheckService extends Service[Request, Response] {
  def apply(request: Request): Future[Response] = Future.value {
    http.Response(Status.Ok)
  }
}
