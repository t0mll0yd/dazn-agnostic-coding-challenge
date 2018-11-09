package com.example.daznagnosticcodingchallenge.responses

case class Error(code: String, message: String)
case class ErrorResponse(error: Error)
