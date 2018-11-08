package com.example.daznagnosticcodingchallenge.stores

import com.twitter.util.Future

abstract class Store {
  def getStreams(userId: String): Future[Set[String]]

  def addStream(userId: String, streamId: String): Future[Unit]
}
