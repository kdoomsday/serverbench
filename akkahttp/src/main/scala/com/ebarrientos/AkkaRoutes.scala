package com.ebarrientos

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import io.circe.syntax._
import zio.Task

/** All routes to use for Akka routing
  *
  * @param dataDao [[DataDao]] for access to info
  */
class AkkaRoutes(dataDao: DataDao) {

  /** Data Routes */
  def routes(): Route =
    dataRoutes()

  private def dataRoutes() =
    concat(
      get {
        pathPrefix("data" / IntNumber) { id =>
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

  /** Helper to convert tasks into values. Uses [[unsafeRun]] to run the task
    * and get the value
    *
    * @param t The [[Task]]
    * @return Result of running the [[Task]]
    */
  @inline private def runZio[T](t: => Task[T]): T =
    zio.Runtime.default.unsafeRun(t)
}
