package com.ebarrientos

import zio._
import zhttp.http._
import zhttp.service.Server
import zio.console._
import zhttp.service.server.ServerChannelFactory
import zhttp.service.EventLoopGroup

object Zioserver extends zio.App {
  private val PORT    = 9000
  private val THREADS = 4

  val dataroute: Http[Any, Throwable] = Http.collect {
    case Method.GET -> Root / "data" / id =>
      Response.text(s"Texto: $id")
  }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    (Server.port(PORT) ++ Server.disableLeakDetection ++ Server.app(dataroute))
      .make
      .use(_ => putStrLn(s"Starting server on port $PORT") *> ZIO.never)
      .provideCustomLayer(
        ServerChannelFactory.auto ++ EventLoopGroup.auto(THREADS)
      )
      .raceFirst(shutdownTask)
      .exitCode

  // Task that finishes on input so it can be raced with the server
  val shutdownTask =
    (for {
      _ <- getStrLn
      _ <- putStrLn("Shutting down server")
    } yield "OK!")
      .catchAll(e => Task.effectTotal(e.getMessage()))
      .flatMap(t => putStrLn(s"Result: $t"))
}
