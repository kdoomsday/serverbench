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
import fs2.concurrent.SignallingRef
import zio.interop.catz
import zio.console._

object Server extends zio.App {

  /** Waits for a line and then changes the shutdown signal to true
    *
    * @param ref shutdown signal
    * @return Task that performs the stated action
    */
  private def shutdownOnEOL(ref: SignallingRef[Task, Boolean]) =
    for {
      _ <- getStrLn
      _ <- ref.set(true)
    } yield ()

  private def mkServer(): ZIO[Console, Throwable, Unit] =
    ZIO
      .runtime[Any]
      .flatMap(implicit rts =>
        for {
          tokenRef <- Ref.make(UUID.randomUUID().toString())

          // Daos
          dao     = new DataDaoImp()
          userDao = new UserDaoDummy(tokenRef)

          // Services
          dataService   = new DataService(dao)
          loginService  = new LoginService(userDao)
          auth          = new Auth(userDao)
          authedService = new AuthedService(auth)

          // App that combines services
          httpApp: HttpApp[Task] = (dataService.dataService
                                     <+> loginService.dataService
                                     <+> authedService.service).orNotFound

          // For shutdown
          exitRef               <- cats
                                     .effect
                                     .concurrent
                                     .Ref
                                     .of(cats.effect.ExitCode(0))(catz.taskEffectInstance)
          signalRef             <- SignallingRef[Task, Boolean](false)
          _                     <- shutdownOnEOL(signalRef).fork

          server <- BlazeServerBuilder
                      .apply[Task](scala.concurrent.ExecutionContext.global)
                      .bindHttp(9000, "localhost")
                      .withHttpApp(httpApp)
                      .serveWhile(signalRef, exitRef)
                      .compile
                      .drain
        } yield server
      )

  def run(args: List[String]) =
    mkServer().catchAll(_ => Task.succeed(ExitCode(2))).map(_ => ExitCode(0))
}
