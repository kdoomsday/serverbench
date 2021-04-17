package com.ebarrientos

import zhttp.http._
import io.circe.syntax._
import io.circe.generic.auto._

import scala.math.{ min, max }

class Dataroute(dao: DataDao) {

  val routes: Http[Any, Throwable] = Http.collectM {
    case Method.GET -> Root / "data" / id =>
      dao.getOne(id.toInt).map(_.asJson.toString()).map(d => Response.jsonString(d))

    case Method.GET -> Root / "data" / "list" / n =>
      val fixedN = max(min(n.toInt, 100), 0)
      dao.getList(fixedN).map(ds => Response.jsonString(ds.asJson.toString()))
  }
}
