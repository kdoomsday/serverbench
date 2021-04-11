package com.ebarrientos

import zio.Task
import org.http4s.server.blaze.BlazeServerBuilder
import zio.interop.catz._
import zio.interop.catz.implicits._
import zio.ZIO
import zio.ExitCode
import cats.implicits._
import org.http4s.HttpApp
import org.http4s.implicits._

object Server extends zio.App {
  val dao = new DataDaoImp()
  val dataService = new DataService(dao)
  val loginService = new LoginService(UserDaoDummy)

  val httpApp: HttpApp[Task] = (dataService.dataService <+> loginService.dataService).orNotFound

  private def mkServer(): ZIO[Any, Throwable, Unit] =
    ZIO.runtime[Any].flatMap(implicit rts =>
      BlazeServerBuilder
        .apply[Task](scala.concurrent.ExecutionContext.global)
        .bindHttp(9000, "localhost")
        .withHttpApp(httpApp)
        .serve
        .compile
        .drain
    )

  def run(args: List[String]) =
    mkServer().catchAll(_ => Task.succeed(ExitCode(2))).map(_ => ExitCode(1))
}
