package com.ebarrientos

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import zio.Task
import io.circe.syntax._
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import com.ebarrientos.Encoders.UserEncoders._
import akka.http.scaladsl.model.headers.Authorization
import java.util.UUID

/** All routes to use for Akka routing
  *
  * @param dataDao [[DataDao]] for access to info
  */
class AkkaRoutes(dataDao: DataDao, userDao: UserDao) {

  /** Data Routes */
  def routes(): Route =
    dataRoutes ~ loginRoutes ~ authedRoutes

  private def dataRoutes: Route =
    concat(
      get {
        path("data" / LongNumber) { id =>
          complete(
            HttpEntity(
              ContentTypes.`application/json`,
              runZio(dataDao.getOne(id)).asJson.toString()
            )
          )
        }
      },
      get {
        path("data" / "list" / IntNumber) { n =>
          complete(
            HttpEntity(
              ContentTypes.`application/json`,
              runZio(dataDao.getList(n)).asJson.toString()
            )
          )
        }
      }
    )

  private def loginRoutes: Route =
    post {
      path("login") {
        entity(as[LoginRequest]) { loginReq =>
          runZio {
            userDao
              .login(loginReq)
              .map { ou =>
                ou match {
                  case None       => complete(StatusCodes.Forbidden, "Unauthorized user")
                  case Some(user) => complete(StatusCodes.OK, user.asJson)
                }
              }
          }
        }
      }
    }

  private def authedRoutes: Route =
    (get & path("secureData")) {
      optionalHeaderValueByName(Authorization.name) {
        case Some(token) =>
          runZio(userDao.validateToken(token)) match {
            case Some(u) =>
              println(s"Token was $token")
              complete(StatusCodes.OK, s"Secret data for [${u.name}]: ${UUID.randomUUID().toString()}")

            case None =>
              complete(StatusCodes.Forbidden, "Invalid Token")
          }

        case None =>
          complete(StatusCodes.Forbidden, "No authorizartion headers")
      }
    }

  /** Helper to convert tasks into values. Uses [[unsafeRun]] to run the task
    * and get the value
    *
    * @param t The [[Task]]
    * @return Result of running the [[Task]]
    */
  @inline private def runZio[T](t: => Task[T]): T =
    zio.Runtime.default.unsafeRun(t)
}
