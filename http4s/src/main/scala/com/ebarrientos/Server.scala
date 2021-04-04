package com.ebarrientos

import cats.effect.Blocker
import zio.Task
import org.http4s.server.blaze.BlazeServerBuilder
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.ZIO
import zio.ExitCode

object Server extends zio.App {
  val dao = new DataDaoImp()
  val dataService = new DataService(dao)

  private def mkServer(): ZIO[Any, Throwable, Unit] =
    ZIO.runtime[Any].flatMap(implicit rts =>
      BlazeServerBuilder[Task]
        .bindHttp(9000, "localhost")
        .withHttpApp(dataService.app)
        .serve
        .compile
        .drain
    )

  def run(args: List[String]) =
    mkServer().catchAll(_ => Task.succeed(ExitCode(2))).map(_ => ExitCode(1))
}
