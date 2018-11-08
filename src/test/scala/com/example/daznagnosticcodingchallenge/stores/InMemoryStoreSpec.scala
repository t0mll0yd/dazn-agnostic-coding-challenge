package com.example.daznagnosticcodingchallenge.stores

import org.specs2.mutable.Specification


class InMemoryStoreSpec extends Specification {
  "InMemoryStore" in {
    "Returns empty set if user has no streams" in {
      val store = InMemoryStore()

      store.getStreams("1") must beEqualTo(Set())
    }
  }
}
