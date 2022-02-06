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

  val routes: HttpApp[Any, Throwable] =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "data" / id =>
        ZIO.effectTotal(log.debug(s"Get data id=$id")) *>
          dao
            .getOne(BigInt(id))
            .map(_.asJson.toString())
            .map(d => Response.json(d))
            .catchAll(_ =>
              ZIO.succeed(
                Response.text(s"Bad id: $id").setStatus(Status.BAD_REQUEST)
              )
            )

      case Method.GET -> !! / "data" / "list" / n =>
        ZIO
          .effect(max(min(n.toInt, 100), 0)) // Ensure n in [0, 100]
          .flatMap(fixedN =>
            ZIO.effectTotal(log.debug(s"Get data list n=$fixedN")) *>
              dao
                .getList(fixedN)
                .map(ds => Response.json(ds.asJson.toString()))
                .catchAll(_ =>
                  ZIO.succeed(
                    Response.text(s"Bad n: $n").setStatus(Status.BAD_REQUEST)
                  )
                )
          )
    }
}
