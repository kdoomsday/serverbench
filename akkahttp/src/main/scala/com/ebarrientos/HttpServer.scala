package com.ebarrientos

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import io.circe.generic.auto._
import io.circe.syntax._
import zio._
import zio.console._
import scala.concurrent.Future
import scala.concurrent.duration._

object HttpServerRoutingMinimal extends zio.App {
  implicit val system = ActorSystem(Behaviors.empty, "my-system")

  def route(dataDao: DataDao): Route =
    pathPrefix("data" / IntNumber) { id =>
      get {
        complete(
          HttpEntity(
            ContentTypes.`application/json`,
            zio.Runtime.default.unsafeRun(dataDao.getOne(id)).asJson.toString
          )
        )
      }
    }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val s: ZIO[Any, Throwable, Http.ServerBinding] =
      for {
        (dataDao, userDao) <- mkDaos()
        server             <- ZIO.fromFuture { _ => serverBinding(dataDao) }
      } yield server

    monitorTask(s)
  }

  def serverBinding(dataDao: DataDao): Future[Http.ServerBinding] = {
    Http().newServerAt("localhost", 9000).bind(route(dataDao))
  }

  /** Build all the necessary Daos for the application */
  private def mkDaos(): ZIO[Any, Nothing, (DataDaoImp, UserDaoDummy)] =
    for {
      tokenRef <- Ref.make[Option[String]](None)
      // Daos
      dao       = new DataDaoImp()
      userDao   = new UserDaoDummy(tokenRef)

    } yield (dao, userDao)

  def monitorTask(activeBinding: ZIO[Any, Throwable, Http.ServerBinding]): ZIO[Console,Nothing,ExitCode] =
    (for {
      sb <- activeBinding
            _  <- putStr("Server ready.")
      _  <- getStrLn
      _  <- putStrLn("Shutting down server")
      _  <- shutDownAll(sb)
    } yield ExitCode.success)
      .catchAll(_ => Task.effectTotal(ExitCode.failure))

  /** Close all resources */
  def shutDownAll(serverBinding: Http.ServerBinding) = Task {
    serverBinding.addToCoordinatedShutdown(10.seconds)
    system.terminate()
  }
}
