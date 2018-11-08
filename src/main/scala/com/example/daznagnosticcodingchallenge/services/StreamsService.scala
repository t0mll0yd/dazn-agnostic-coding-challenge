package com.example.daznagnosticcodingchallenge.services

import com.example.daznagnosticcodingchallenge.stores.Store
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future

case class StreamsService(store: Store, userId: String, streamId: String) extends Service[Request, Response] {
  def apply(request: Request): Future[Response] =
    store.getStreams(userId).map(streams => {
      if (streams.contains(streamId)) {
        Response(Status.Ok)
      } else if (streams.size > 2) {
        Response(Status.Conflict)
      } else {
        Response(Status.Created)
      }
    })

}
