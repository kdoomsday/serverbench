package com.ebarrientos

import java.util.UUID
import zhttp.http._
import zio.Task

class Authedroute(authmiddle: AuthMiddleware) {

  val routes = Http.collectZIO[Request] { case req @ Method.GET -> !! / "secureData" =>
    authmiddle(req)
      .map { u =>
        Response.text(
          s"Secret data for [${u.data.name}]: ${UUID.randomUUID().toString()}"
        )
      }
      .catchAll(msg =>
        Task.succeed(Response.fromHttpError(HttpError.Forbidden(msg)))
      )
  }
}
