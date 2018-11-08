package com.example.daznagnosticcodingchallenge.responses

case class ErrorResponse(code: String, message: String) {

  // If parsing and creating JSON was a common occurrence throughout the codebase,
  // I would use a JSON serialisation library for this (such as JSON4S).
  val asJson: String =
    s"""
      |{
      |   "code": $code,
      |   "message": $message
      |}
    """.stripMargin
}
