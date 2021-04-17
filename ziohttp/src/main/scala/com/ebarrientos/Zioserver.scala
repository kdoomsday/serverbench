package com.ebarrientos

import zio._
import zhttp.http._
import zhttp.service.Server
import zio.console._

object Zioserver extends zio.App {

  val dataroute = Http.collect { case Method.GET -> Root / "data" / id =>
    Response.text(s"Texto: $id")
  }

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    Server
      .start(9000, dataroute)
      .raceFirst(shutdownTask)
      .exitCode

  // val shutdownTask = getStrLn.catchAll(_ => Task.effectTotal(""))
  val shutdownTask =
    (for {
      _ <- getStrLn
      _ <- getStrLn  // Need a second one because the first one goes right on by
      _ <- putStrLn("Shutting down server")
    } yield "OK!")
      .catchAll(e => Task.effectTotal(e.getMessage()))
      .flatMap(t => putStrLn(s"Result: $t"))
}
