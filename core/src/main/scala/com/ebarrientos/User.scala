package com.ebarrientos

/** A user in the systema
  *
  * @param id           UserId
  * @param name         The user's name
  * @param login        Login
  * @param passwordHash stored password hash
  * @param salt         password salt
  */
case class User(id: UserId, name: String, login: String, passwordHash: String, salt: Int)


/* Classes related to login actions */
case class UserId(id: Long)                extends AnyVal
case class Login(login: String)            extends AnyVal
case class ClearPassword(password: String) extends AnyVal

/** A request for authentication
  *
  * @param login         The login that is being attempted
  * @param clearPassword The password in clear text
  */
case class LoginRequest(login: Login, clearPassword: ClearPassword)
