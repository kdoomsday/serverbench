package com.ebarrientos

import io.circe.syntax._
import io.circe.parser.decode
import io.circe.generic.auto._

import zhttp.http._

import zio.Task
import zio.ZIO

import Encoders.UserEncoders._

/** Routes to handle login operations */
class Loginroute(dao: UserDao) {

  val routes: HttpApp[Any, Throwable] = Http.collectZIO {
    case req @ Method.POST -> !! / "login" =>
      reqAsLR(req)
        .flatMap(lr => dao.login(lr))
        .map(_.map(user2response))
        .map(
          _.fold(Response.fromHttpError(HttpError.Forbidden("Invalid login credentials")))(u =>
            Response.json(u.asJson.toString())
          )
        )
  }

  private def user2response(u: User): LoginResponse =
    LoginResponse(login = u.login, name = u.name, token = u.token)

  /** Extract the login request from the request, handling errors as we go along
    *
    * @param req [[Request]]
    * @return Extracted [[LoginRequest]] as a [[Task]]. Errors mean a failed task
    */
  private def reqAsLR(req: Request): Task[LoginRequest] =
    ZIO.absolve(
      req.getBodyAsString
        .mapError(_ => new Exception("No request body"))
        .map(decode[LoginRequest])
    )
}
