package com.ebarrientos

import zhttp.http.Request
import zio.IO
import zio.Task
import zio.ZIO

/** Middleware that authenticates a user using a UserDao
  * If authentication is succesful, provides the user
  * On failure provides a message indicating the cause
  *
  * @param dao
  */
class AuthMiddleware(dao: UserDao) extends Middleware[String, User] {

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
