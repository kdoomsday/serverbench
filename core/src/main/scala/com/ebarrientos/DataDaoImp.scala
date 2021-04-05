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

  /** @return [[getOne(id)]] with a random positive id */
  private def getOneNoId() =
    getOne(BigDecimal(Random.between(1, Int.MaxValue)))

  def getList(n: Int): Task[Seq[Data]] =
    if (n < 1)
      getOneNoId().map(bd => Seq(bd))
    else
      Task.collectAllPar((1 until(n)).map(_ => getOneNoId()))
}
