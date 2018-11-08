package com.example.daznagnosticcodingchallenge.services

import com.example.daznagnosticcodingchallenge.stores.Store
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

case class StreamsService(store: Store, userId: String, streamId: String) extends Service[Request, Response] {
  val MaximumConcurrentStreams = 3

  val hasStreamResponse: Future[Response] = Future.value {
    Response(Status.Ok)
  }

  val maxStreamsResponse: Future[Response] = Future.value {
    Response(Status.Conflict)
  }

  def addStream(): Future[Response] =
    store.addStream(userId, streamId).map {
      _ => Response(Status.Created)
    }

  def apply(request: Request): Future[Response] =
    store.getStreams(userId).flatMap(streams => {
      if (streams.contains(streamId)) hasStreamResponse
      else if (streams.size >= MaximumConcurrentStreams) maxStreamsResponse
      else addStream()
    })

}
