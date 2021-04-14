package com.ebarrientos

import zio.Task
import zio.Ref
import java.util.UUID
import zio.UIO

object UserDaoDummy extends UserDao {
  private val tokenRef: UIO[Ref[String]] = Ref.make(UUID.randomUUID().toString())

  private def user(tok: String) = User(
    UserId(1L),
    Token(tok),
    "User Person",
    Login("user"),
    "866b621764540ba90a1776939f8f3b945b3597f1",
    1
  )

  override def login(loginReq: LoginRequest): Task[Option[User]] =
    if (
      loginReq.login == Login("user")
      && loginReq.password == ClearPassword("password")
    ) {
      val newToken = UUID.randomUUID().toString()
      tokenRef.flatMap(t =>
        t.update(_ => newToken)
          *> Task.succeed(Some(user(newToken)))
      )
    }
    else {
      Task.succeed(None)
    }


  def validateToken(token: String): Task[Option[User]] =
    for {
      ref <- tokenRef
      v  <- ref.get
    } yield if (v == token) Some(user(v)) else None
}
