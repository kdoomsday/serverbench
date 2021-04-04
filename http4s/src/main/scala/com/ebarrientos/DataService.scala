package com.ebarrientos

import org.http4s.HttpRoutes
import zio.Task
import zio.interop.catz._
import zio.interop.catz.implicits._

import cats.implicits._

import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.implicits._

// TODO Run as task without having to unsafeRun here
class DataService(dao: DataDao) {
  private val dsl = new Http4sDsl[Task] {}
  import dsl._

  // implicit def circeJsonEncoder[A](implicit decoder: Encoder[A]):
  //     EntityEncoder[Task, A] = jsonEncoderOf[Task, A]

  // implicit def dataEncoder: Encoder[Data] = deriveEncoder
  // implicit def dataDecoder: Decoder[Data] = deriveDecoder

  val r = zio.Runtime.default

  val dataService = HttpRoutes.of[Task] {
    case GET -> Root / "data" / IntVar(id) =>
      dao.getOne(id).flatMap(data => Ok(data.asJson))
  }

  val app: HttpApp[Task] = dataService.orNotFound
}
