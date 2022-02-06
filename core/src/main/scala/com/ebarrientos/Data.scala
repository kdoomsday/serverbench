package com.ebarrientos

case class Address(street: String, zip: String)

case class Data(id: BigInt, name: String, address: Address)
