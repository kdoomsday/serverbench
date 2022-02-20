package com.ebarrientos

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import zio._
import zio.console._
import scala.concurrent.Future
import scala.concurrent.duration._

object HttpServerRoutingMinimal extends zio.App {
  implicit val system = ActorSystem(Behaviors.empty, "my-system")

  override def run(args: List[String]): URIO[ZEnv, ExitCode] = {
    val s: ZIO[Any, Throwable, Http.ServerBinding] =
      for {
        (dataDao, userDao) <- mkDaos()
        server             <- ZIO.fromFuture { _ => serverBinding(dataDao, userDao) }
      } yield server

    monitorTask(s)
  }


  /** Creates and binds a server
    *
    * @param dataDao Server dependencies
    * @return The [[Http.ServerBinding]] as a [[Future]]
    */
  def serverBinding(dataDao: DataDao, userDao: UserDao): Future[Http.ServerBinding] = {
    val akkaRoute = new AkkaRoutes(dataDao, userDao)
    Http().newServerAt("localhost", 9000).bind(akkaRoute.routes())
  }


  /** Build all the necessary Daos for the application */
  private def mkDaos(): ZIO[Any, Nothing, (DataDaoImp, UserDaoDummy)] =
    for {
      tokenRef <- Ref.make[Option[String]](None)
      // Daos
      dao       = new DataDaoImp()
      userDao   = new UserDaoDummy(tokenRef)

    } yield (dao, userDao)


  /** Task to handle the server.
    * Starts the server then waits to allow a clean shutdown. On receiving
    * <Enter> will shutdown the given binding and exit the application
    *
    * @param activeBinding
    * @return
    */
  def monitorTask(
      activeBinding: ZIO[Any, Throwable, Http.ServerBinding]
  ): ZIO[Console, Nothing, ExitCode] =
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
