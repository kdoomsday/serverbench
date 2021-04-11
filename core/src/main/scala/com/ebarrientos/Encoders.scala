package com.ebarrientos

import io.circe._
import io.circe.generic.extras.semiauto._

object Encoders {

  object UserEncoders {

    implicit val loginEncoder: Encoder[Login] = deriveUnwrappedEncoder
    implicit val loginDecoder: Decoder[Login] = deriveUnwrappedDecoder

    implicit val clearPasswordEncoder: Encoder[ClearPassword] = deriveUnwrappedEncoder
    implicit val clearPasswordDecoder: Decoder[ClearPassword] = deriveUnwrappedDecoder
  }
}
