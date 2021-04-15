package com.ebarrientos

import cats.data.Kleisli
import cats.data.OptionT
import cats.implicits._

import org.http4s._
import org.http4s.dsl._

import zio.Task
import zio.interop.catz._
import org.http4s.server.AuthMiddleware
import org.http4s.util.CaseInsensitiveString

/** Handles authenticating a request by authorization headers
  *
  * @param dao [[UserDao]] to validate the token
  */
class Auth(dao: UserDao) {
  private val dsl = new Http4sDsl[Task] {}
  import dsl._

  val authUser: Kleisli[Task, Request[Task], Either[String, User]] =
    Kleisli({ request =>
      // request.headers.foreach(h => println(s"Header: $h"))
      // println("------------------------------------------------------------")

      val res: Either[String, Header] =
        request
          .headers
          .get(CaseInsensitiveString("Authorization"))
          .toRight("No authorization headers")

      res.flatTraverse(t =>
        dao.validateToken(t.value).map(_.toRight("Invalid token"))
      )
    })

  val onFailure: AuthedRoutes[String, Task] =
    Kleisli(req => OptionT.liftF(Forbidden(req.context)))
  val middleware                            = AuthMiddleware(authUser, onFailure)
}
