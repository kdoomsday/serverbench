package com.ebarrientos

import org.http4s.HttpRoutes
import zio.Task
import zio.interop.catz._

import io.circe.syntax._
import io.circe.generic.auto._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.implicits._

import scala.math.{max, min}
import com.typesafe.scalalogging.Logger
import zio.ZIO

class DataService(dao: DataDao) {
  val log = Logger[DataService]

  private val dsl = new Http4sDsl[Task] {}
  import dsl._

  val r = zio.Runtime.default

  val dataService = HttpRoutes.of[Task] {
    case GET -> Root / "data" / IntVar(id) =>
      ZIO.effectTotal(log.debug(s"Get data id=$id")) *>
        dao.getOne(id).flatMap(data => Ok(data.asJson))

    case GET -> Root / "data" / "list" / IntVar(n) =>
      val fixedN = max(min(n, 100), 0)
      ZIO.effectTotal(log.debug(s"Get data list n=$fixedN")) *>
        dao.getList(fixedN).flatMap(ds => Ok(ds.asJson))
  }

  val app: HttpApp[Task] = dataService.orNotFound
}
