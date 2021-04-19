package com.ebarrientos

import java.util.UUID
import zhttp.http._
import zio.ZIO
import zio.{IO, Task}

class Authedroute(dao: UserDao) {

  val authmiddle = new Middleware[String, User] {

    def apply(req: Request) = {
      // Option[Nothing] when no headers
      // Task[Option[User]] when headers found
      val tou: IO[Option[Nothing], Task[Option[User]]] = ZIO
        .fromOption(
          req
            .headers
            .filter(h => h.name == "Authorization")
            .headOption
            .map(h => dao.validateToken(h.value.toString()))
        )

      tou
        .mapError(_ => "No authorization headers")
        .flatMap(tu =>
          tu.map(ou => ou.map(u => Wrapped(req, u)))
            .mapError(_.getMessage())
            .map(_.toRight("User not found"))
            .absolve
        )
    }
  }

  val routes = Http.collectM { case req @ Method.GET -> Root / "secureData" =>
    authmiddle(req)
      .map(u => {
        Response.text(
          s"Secret data for [${u.data.name}]: ${UUID.randomUUID().toString()}"
        )
      })
    .catchAll(msg => Task.succeed(Response.fromHttpError(HttpError.Forbidden(msg))))
  }
}
