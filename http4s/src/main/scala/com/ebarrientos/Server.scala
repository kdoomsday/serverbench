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

  private def mkServer(): ZIO[Any, Throwable, Unit] =
    ZIO
      .runtime[Any]
      .flatMap(implicit rts =>
        for {
          tokenRef              <- Ref.make(UUID.randomUUID().toString())
          dao                    = new DataDaoImp()
          userDao                = new UserDaoDummy(tokenRef)
          dataService            = new DataService(dao)
          loginService           = new LoginService(userDao)
          auth                   = new Auth(userDao)
          authedService          = new AuthedService(auth)
          httpApp: HttpApp[Task] =
            (dataService.dataService <+> loginService.dataService <+> authedService.service).orNotFound
          server                <- BlazeServerBuilder
                                     .apply[Task](scala.concurrent.ExecutionContext.global)
                                     .bindHttp(9000, "localhost")
                                     .withHttpApp(httpApp)
                                     .serve
                                     .compile
                                     .drain
        } yield server
      )

  def run(args: List[String]) =
    mkServer().catchAll(_ => Task.succeed(ExitCode(2))).map(_ => ExitCode(1))
}
