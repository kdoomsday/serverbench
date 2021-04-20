package com.ebarrientos

import zio.Task
import java.util.UUID
import zio.Ref
import com.typesafe.scalalogging.Logger

class UserDaoDummy(tokenRef: Ref[Option[String]]) extends UserDao {
  val log = Logger[UserDaoDummy]

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
      log.debug(s"Successful login for ${loginReq.login}")
      val newToken = UUID.randomUUID().toString()
      for {
        tok <- tokenRef.modify(_ => (newToken, Some(newToken)))
      } yield Some(user(tok))
    }
    else {
      log.info(s"Invalid attempted login for ${loginReq.login}")
      Task.succeed(None)
    }

  def validateToken(token: String): Task[Option[User]] = {
    // val res = for {
    //   v <- tokenRef.get
    // } yield v.fold[Option[User]](None)(t =>
    //   if (t == token) Some(user(t)) else None
    // )

    // res

    // val res = for {
    //   v <- tokenRef.get
    // } yield v.map(t => if (t == token) Some(user(t)) else None)

    // res.map(_.flatten)

    (for {
      v <- tokenRef.get
    } yield v.map(t => if (t == token) Some(user(t)) else None))
      .map(_.flatten)
  }
}
