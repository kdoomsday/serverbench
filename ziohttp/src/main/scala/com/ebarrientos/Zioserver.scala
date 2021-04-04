package com.ebarrientos

import zio._
import zhttp.http._
import zhttp.service.Server

object Zioserver extends zio.App {

  val dataroute = Http.collect {
    case Method.GET -> Root / "data" / id =>
      Response.text("texto")
  }

  override def run(args: List[String]): URIO[ZEnv,ExitCode] =
    Server.start(9000, dataroute).exitCode

}
