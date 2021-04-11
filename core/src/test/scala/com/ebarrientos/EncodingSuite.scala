package com.ebarrientos

import org.scalatest.funsuite.AnyFunSuite

import io.circe.syntax._
import io.circe.parser.decode
import io.circe.generic.auto._

import Encoders.UserEncoders._

class EncodingSuite extends AnyFunSuite {
  val lr = LoginRequest(Login("eduardo"), ClearPassword("password"))
  val str = """|{
               |  "login" : "eduardo",
               |  "password" : "password"
               |}""".stripMargin

  test("Decode login request") {
    val res = decode[LoginRequest](str)

    val expected: Either[io.circe.Error, LoginRequest] = Right(lr)

    if (res.isRight) {
      assert(res == expected)
    }
    else {
      println(res)
      fail(s"Expected correct decoding but got: ${res.left.toOption.getOrElse("Unknown")}")
    }
  }

  test("Encode login request") {
    val encoded = lr.asJson.toString()

    assert(encoded == str)
  }
}
