package com.ebarrientos

import zio.Task
import zio.Ref
import java.util.UUID
import zio.UIO

object UserDaoDummy extends UserDao {
  private val token: UIO[Ref[String]] = Ref.make(UUID.randomUUID().toString())

  override def login(loginReq: LoginRequest): Task[Option[User]] =
    if (
      loginReq.login == Login("user")
        && loginReq.password == ClearPassword("password")
    ) {
      val newToken = UUID.randomUUID().toString()
      token.flatMap(t =>
        t.update(_ => newToken) *> Task.succeed(
          Some(
            User(
              UserId(1L),
              Token(newToken),
              "User Person",
              Login("user"),
              "866b621764540ba90a1776939f8f3b945b3597f1",
              1
            )
          )
        )
      )
    }
    else {
      Task.succeed(None)
    }

}
