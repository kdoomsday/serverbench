package com.ebarrientos

import zio.Task
import zio.interop.catz._

import io.circe.syntax._
import io.circe.generic.auto._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._

import com.ebarrientos.Encoders.UserEncoders._

/** Handles user login
  *
  * @param dao [[UserDao]] for authenticating the user
  */
class LoginService(dao: UserDao) {
  private val dsl = new Http4sDsl[Task] {}
  import dsl._

  implicit val decoder: EntityDecoder[Task, LoginRequest] =
    jsonOf[Task, LoginRequest]

  val dataService = HttpRoutes.of[Task] { case req @ POST -> Root / "login" =>
    req
      .as[LoginRequest]
      .flatMap(lr => dao.login(lr))
      .map(_.map(user2response))
      .flatMap(_.fold(Forbidden())(u => Ok(u.asJson)))
  }

  private def user2response(u: User): LoginResponse =
    LoginResponse(login = u.login, name = u.name, token = u.token)
}
