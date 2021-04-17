package com.ebarrientos

import zio._
import zhttp.service.Server
import zio.console._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.EventLoopGroup

object Zioserver extends zio.App {
  private val PORT    = 9000
  private val THREADS = 4

  val dataroute = Task {
    val dataDao = new DataDaoImp()
    new Dataroute(dataDao)
  }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    server
      .raceFirst(shutdownTask)
      .exitCode

  val server =
    (for {
      dr <- dataroute
    } yield (Server.port(PORT)
      ++ Server.disableLeakDetection
      ++ Server.app(dr.routes))
      .make
      .use(_ => putStrLn(s"Starting server on port $PORT") *> ZIO.never)
      .provideCustomLayer(
        ServerChannelFactory.auto ++ EventLoopGroup.auto(THREADS)
      ))
      .flatten

  // Task that finishes on input so it can be raced with the server
  val shutdownTask =
    (for {
      _ <- getStrLn
      _ <- putStrLn("Shutting down server")
    } yield "OK!")
      .catchAll(e => Task.effectTotal(e.getMessage()))
      .flatMap(t => putStrLn(s"Result: $t"))
}
