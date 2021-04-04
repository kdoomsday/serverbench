package com.ebarrientos

case class Address(street: String, zip: String)

case class Data(id: BigDecimal, name: String, address: Address)
