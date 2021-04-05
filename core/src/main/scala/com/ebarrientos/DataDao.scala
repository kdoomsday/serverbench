package com.ebarrientos

import zio.Task

trait DataDao {
  def getOne(id: BigDecimal): Task[Data]

  def getList(n: Int): Task[Seq[Data]]
}
