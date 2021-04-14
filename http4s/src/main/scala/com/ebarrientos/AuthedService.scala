package com.ebarrientos

import org.http4s.AuthedRoutes
import org.http4s.dsl._

import java.util.UUID

import zio.Task
import zio.interop.catz._
import org.http4s.HttpRoutes

/** Routes that require authentication
  *
  * @param auth [[Auth]] with the necessary middleware
  */
class AuthedService(auth: Auth) {
  private val dsl = new Http4sDsl[Task] {}
  import dsl._

  val authedRoutes: AuthedRoutes[User, Task] = AuthedRoutes.of {
    case GET -> Root / "secureData" as user => {
      println(s"Authenticated as ${user.login}")
      Ok(UUID.randomUUID().toString())
    }
  }

  val service: HttpRoutes[Task] = auth.middleware(authedRoutes)
}

