package com.example.daznagnosticcodingchallenge

import com.example.daznagnosticcodingchallenge.services.Routes
import com.twitter.finagle.Http
import com.twitter.util.Await

object Server extends App {

  Await.ready {
    Http.serve(":8080", Routes.makeService())
  }
}
