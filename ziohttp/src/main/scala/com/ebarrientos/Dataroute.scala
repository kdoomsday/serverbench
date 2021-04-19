package com.ebarrientos

import com.typesafe.scalalogging.Logger
import io.circe.generic.auto._
import io.circe.syntax._
import zhttp.http._
import zio.ZIO

import scala.math.max
import scala.math.min

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
