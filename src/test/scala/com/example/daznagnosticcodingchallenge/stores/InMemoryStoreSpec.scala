package com.example.daznagnosticcodingchallenge.stores

import com.twitter.util.Await
import org.specs2.mutable.Specification


class InMemoryStoreSpec extends Specification {
  "InMemoryStore" in {
    "Returns empty set if user has no streams" in {
      val store = InMemoryStore()

      val response = Await.result {
        store.getStreams("userId")
      }

      response must beEqualTo(Set())
    }

    "Returns streams that have been added" in {
      val store = InMemoryStore()

      val response = Await.result {
        for {
          _ <- store.addStream("user1", "stream1")
          _ <- store.addStream("user1", "stream2")
          _ <- store.addStream("user1", "stream3")
          streams <- store.getStreams("user1")
        } yield streams
      }

      response must beEqualTo(Set("stream1", "stream2", "stream3"))
    }

    "Does not return streams for other users" in {
      val store = InMemoryStore()

      val response = Await.result {
        for {
          _ <- store.addStream("user1", "stream1")
          _ <- store.addStream("user2", "stream2")
          _ <- store.addStream("user3", "stream3")
          streams <- store.getStreams("user1")
        } yield streams
      }

      response must beEqualTo(Set("stream1"))
    }
  }
}
