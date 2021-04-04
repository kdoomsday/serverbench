import zio._
import zhttp.http._
import zhttp.service.Server

object HelloWorld extends App {
  val app = Http.collect {
    case Method.GET -> Root / "text" => Response.text("Hello World!")
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    Server.start(8090, app).exitCode
}
