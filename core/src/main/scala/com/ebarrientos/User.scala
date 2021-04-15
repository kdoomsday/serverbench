package com.ebarrientos

case class UserId(id: Long)                extends AnyVal
case class Token(token: String)            extends AnyVal
case class Login(login: String)            extends AnyVal
case class ClearPassword(password: String) extends AnyVal

/* Classes related to login actions */
/** A user in the systema
  *
  * @param id           UserId
  * @param token        Active user token
  * @param name         The user's name
  * @param login        Login
  * @param passwordHash stored password hash
  * @param salt         password salt
  */
case class User(
    id: UserId,
    token: Token,
    name: String,
    login: Login,
    passwordHash: String,
    salt: Int
)

/** A request for authentication
  *
  * @param login    The login that is being attempted
  * @param password The password in clear text
  */
case class LoginRequest(login: Login, password: ClearPassword)

/**
  * Successful login response
  *
  * @param login User login
  * @param name  User name
  * @param token User token
  */
case class LoginResponse(login: Login, name: String, token: Token)
