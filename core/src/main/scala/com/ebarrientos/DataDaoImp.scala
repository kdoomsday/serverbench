package com.ebarrientos

import zio.Task
import scala.util.Random

class DataDaoImp extends DataDao {

  def getOne(id: BigDecimal): Task[Data] = Task.effect({
    val name   = Random.nextString(Random.between(3, 10))
    val street = Random.nextString(Random.between(5, 20))
    val zip    = Random.between(1000, 9999).toString()
    Data(id, name, Address(street, zip))
  })
}
