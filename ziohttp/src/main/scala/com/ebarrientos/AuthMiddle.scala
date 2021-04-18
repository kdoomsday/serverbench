package com.ebarrientos

import zhttp.http.Request
import zio.Task

case class Wrapped[+A](req: Request, data: A)

/** Middleware to extract info from a request */
trait Middleware[A] {
  def apply(r: Request): Task[Option[Wrapped[A]]]
}
