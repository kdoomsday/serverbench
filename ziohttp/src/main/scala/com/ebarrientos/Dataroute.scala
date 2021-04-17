package com.ebarrientos

import zhttp.http._
import io.circe.syntax._
import io.circe.generic.auto._

class Dataroute(dao: DataDao) {

  val routes: Http[Any, Throwable] = Http.collectM {
    case Method.GET -> Root / "data" / id =>
      data(id).map(d => Response.jsonString(d))
  }

  def data(id: String) =
    dao.getOne(id.toInt).map(_.asJson.toString())
}
