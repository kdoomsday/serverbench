package com.ebarrientos

import zhttp.http.Request
import zio.IO

case class Wrapped[+A](req: Request, data: A)

/** Middleware to extract info from a request
  * If the info cannot be extracted, finishes with an error E
  */
trait Middleware[E, A] {
  def apply(r: Request): IO[E, Wrapped[A]]
}
