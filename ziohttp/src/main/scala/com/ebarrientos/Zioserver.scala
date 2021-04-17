package com.ebarrientos

import zio._
import zhttp.http._
import zhttp.service.Server
import zio.console._

object Zioserver extends zio.App {
  private val PORT = 9000

  val dataroute: Http[Any,Throwable] = Http.collect { case Method.GET -> Root / "data" / id =>
    Response.text(s"Texto: $id")
  }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    Server
      .start(PORT, dataroute)
      .raceFirst(shutdownTask)
      .exitCode

  // Task that finishes on input so it can be raced with the server
  val shutdownTask =
    (for {
      _ <- getStrLn
      _ <- getStrLn  // Need a second one because the first one goes right on by
      _ <- putStrLn("Shutting down server")
    } yield "OK!")
      .catchAll(e => Task.effectTotal(e.getMessage()))
      .flatMap(t => putStrLn(s"Result: $t"))
}
