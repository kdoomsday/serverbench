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
import zio.Ref
import java.util.UUID

object Server extends zio.App {

  val tokenRef =
    zio.Runtime.default.unsafeRun(Ref.make(UUID.randomUUID().toString()))

  val dao     = new DataDaoImp()
  val userDao = new UserDaoDummy(tokenRef)

  val dataService   = new DataService(dao)
  val loginService  = new LoginService(userDao)
  val auth          = new Auth(userDao)
  val authedService = new AuthedService(auth)

  val httpApp: HttpApp[Task] =
    (dataService.dataService <+> loginService.dataService <+> authedService.service).orNotFound

  private def mkServer(): ZIO[Any, Throwable, Unit] =
    ZIO
      .runtime[Any]
      .flatMap(implicit rts =>
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
