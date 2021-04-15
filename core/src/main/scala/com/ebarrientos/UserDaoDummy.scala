package com.ebarrientos

import zio.Task
import java.util.UUID
import zio.Ref

class UserDaoDummy(tokenRef: Ref[String]) extends UserDao {

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
      for {
        tok <- tokenRef.modify(_ => (newToken, newToken))
      } yield Some(user(tok))
      // tokenRef.set(UUID.randomUUID().toString())
      //     *> tokenRef.get.map(tok => Some(user(tok)))
    }
    else {
      Task.succeed(None)
    }

  def validateToken(token: String): Task[Option[User]] =
    for {
      v   <- tokenRef.get
    } yield if (v == token) Some(user(v)) else None
}
