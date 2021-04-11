package com.ebarrientos

import zio.Task

object UserDaoDummy extends UserDao {

  override def login(loginReq: LoginRequest): Task[Option[User]] =
    Task.effect {
      if (loginReq.login == Login("user")
            && loginReq.clearPassword == ClearPassword("password"))
        Some(User(UserId(1L),
                  "User Person",
                  "user",
                  "866b621764540ba90a1776939f8f3b945b3597f1",
                  1))
      else
        None
    }

}
