package com.example.daznagnosticcodingchallenge.services

import com.example.daznagnosticcodingchallenge.responses.{Error, ErrorResponse}
import com.example.daznagnosticcodingchallenge.stores.Store
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write

case class StreamsService(store: Store, userId: String, streamId: String) extends Service[Request, Response] {
  implicit val formats = DefaultFormats

  val MaximumConcurrentStreams = 3

  def hasStreamResponse: Future[Response] = Future.value {
    println("Stream already exists.")
    Response(Status.Ok)
  }

  def maxStreamsResponse: Future[Response] = Future.value {
    println("Maximum concurrent stream limit reached.")

    val response = Response(Status.Conflict)

    response.contentType = "application/json"

    response.contentString = write(ErrorResponse(
      error = Error(
        code = "streams.limit.reached",
        message = "This user already has the maximum number of concurrent streams.",
      )
    ))

    response
  }

  def addStream(): Future[Response] = {
    println("Adding new stream.")

    store.addStream(userId, streamId).map {
      _ => Response(Status.Created)
    }
  }

  def apply(request: Request): Future[Response] = {
    store.getStreams(userId).flatMap(streams => {
      println(s"Received stream $streamId for user $userId. Current streams: [${streams.mkString(", ")}].")

      if (streams.contains(streamId)) hasStreamResponse
      else if (streams.size >= MaximumConcurrentStreams) maxStreamsResponse
      else addStream()
    })
  }

}
