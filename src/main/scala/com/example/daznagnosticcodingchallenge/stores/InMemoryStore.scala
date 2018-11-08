package com.example.daznagnosticcodingchallenge.stores

import com.twitter.util.Future


case class InMemoryStore(initialStore: Map[String, Set[String]] = Map()) extends Store {
  private var store: Map[String, Set[String]] = Map()

  def getStreams(userId: String): Future[Set[String]] = Future.value {
    store.getOrElse(userId, Set())
  }

  def addStream(userId: String, streamId: String): Future[Unit] =
    this.getStreams(userId).map(streams => {
      store += (userId -> (streams + streamId))
    })
}
