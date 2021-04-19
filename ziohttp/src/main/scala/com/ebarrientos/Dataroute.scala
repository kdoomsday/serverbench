package com.ebarrientos

import zhttp.http._
import io.circe.syntax._
import io.circe.generic.auto._

import scala.math.{max, min}
import com.typesafe.scalalogging.Logger
import zio.ZIO

class Dataroute(dao: DataDao) {
  val log = Logger[Dataroute]

  val routes: Http[Any, Throwable] = Http.collectM {
    case Method.GET -> Root / "data" / id =>
      ZIO.effectTotal(log.debug(s"Get data id=$id")) *>
        dao
          .getOne(id.toInt)
          .map(_.asJson.toString())
          .map(d => Response.jsonString(d))

    case Method.GET -> Root / "data" / "list" / n =>
      val fixedN = max(min(n.toInt, 100), 0)
      ZIO.effectTotal(log.debug(s"Get data list n=$fixedN")) *>
        dao.getList(fixedN).map(ds => Response.jsonString(ds.asJson.toString()))
  }
}
