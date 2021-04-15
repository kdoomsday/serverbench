package com.ebarrientos

import zio.Task

trait UserDao {
  def login(loginReq: LoginRequest): Task[Option[User]]

  def validateToken(token: String): Task[Option[User]]
}

