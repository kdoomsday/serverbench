package com.ebarrientos

import zio._
import zhttp.service.Server
import zio.console._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.EventLoopGroup

object Zioserver extends zio.App {
  private val PORT    = 9000
  private val THREADS = 4

  val routes =
    Ref.make[Option[String]](None).map { ref =>
      val dataDao = new DataDaoImp()

      val userDao    = new UserDaoDummy(ref)
      val authmiddle = new AuthMiddleware(userDao)

      val dataroute   = new Dataroute(dataDao)
      val loginroute  = new Loginroute(userDao)
      val authedroute = new Authedroute(authmiddle)

      dataroute.routes ++ loginroute.routes ++ authedroute.routes
    }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    server
      .raceFirst(shutdownTask)
      .exitCode

  val server =
    (for {
      r <- routes
    } yield (Server.port(PORT)
      ++ Server.disableLeakDetection
      ++ Server.app(r))
      .make
      .use(_ => putStrLn(s"Starting server on port $PORT") *> ZIO.never)
      .provideCustomLayer(
        ServerChannelFactory.auto ++ EventLoopGroup.auto(THREADS)
      )).flatten

  // Task that finishes on input so it can be raced with the server
  val shutdownTask =
    (for {
      _ <- getStrLn
      _ <- putStrLn("Shutting down server")
    } yield "OK!")
      .catchAll(e => Task.effectTotal(e.getMessage()))
      .flatMap(t => putStrLn(s"Result: $t"))
}
