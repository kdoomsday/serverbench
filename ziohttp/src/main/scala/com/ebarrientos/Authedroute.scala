package com.ebarrientos

import java.util.UUID
import zhttp.http._
import zio.ZIO

class Authedroute(dao: UserDao) {

  val authmiddle: Middleware[User] = new Middleware[User] {

    def apply(req: Request) = {
      ZIO
        .fromOption(
          req
            .headers
            .filter(h => h.name == "Authorization")
            .headOption
            .map(h => dao.validateToken(h.value.toString()))
        )
        .mapError(_ => new Throwable("No authorization headers"))
        .flatten
        .map(ou => ou.map(u => Wrapped(req, u)))
    }
  }

  val routes = Http.collectM { case req @ Method.GET -> Root / "secureData" =>
    authmiddle(req).map(wr => {
      wr.map(u =>
        Response.text(
          s"Secret data for [${u.data.name}]: ${UUID.randomUUID().toString()}"
        )
      ).getOrElse(Response.fromHttpError(HttpError.Forbidden("Invalid token")))
    })
  }
}
