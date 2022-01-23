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

object HttpServerRoutingMinimal extends zio.App {
  implicit val system = ActorSystem(Behaviors.empty, "my-system")
  // needed for the future flatMap/onComplete in the end
  // implicit val executionContext = system.executionContext

  // TODO
  // def mkSystem(): ZManaged[Any, Throwable, ActorSystem[Any]] =
  //   Managed.make(Task(ActorSystem(Behaviors.empty, "my-system")))(system =>
  //     Task(system.terminate()).ignore
  //   )

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

    s
      .disconnect
      .raceFirst(monitorTask(s).disconnect)
      .exitCode
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

  def monitorTask(activeBinding: ZIO[Any, Throwable, Http.ServerBinding]) =
    (for {
      _  <- putStr("Server ready.")
      _  <- getStrLn
      _  <- putStrLn("Shutting down server")
      sb <- activeBinding
      _  <- shutDownAll(sb)
    } yield "OK!")
      .catchAll(e => Task.effectTotal(e.getMessage()))
      .flatMap(t => putStrLn(s"Result: $t"))

  /** Close all resources */
  def shutDownAll(serverBinding: Http.ServerBinding) = Task {
    serverBinding.unbind()
    system.terminate()
  }
}
