package com.example.daznagnosticcodingchallenge.services

import com.example.daznagnosticcodingchallenge.stores.{InMemoryStore, Store}
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.{BasicAuth, Method, Request}

object Routes {
  private val BasicAuthFilter = BasicAuth.serverFromCredentials("username", "password")

  def makeService(store: Store = InMemoryStore()): RoutingService[Request with Request] = RoutingService.byMethodAndPathObject[Request] {
    case Method.Get -> Root / "health-check" => HealthCheckService
    case Method.Post -> Root / "users" / userId / "streams" / streamId =>
      BasicAuthFilter andThen StreamsService(store, userId, streamId)
  }
}
