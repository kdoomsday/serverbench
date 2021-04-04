package com.ebarrientos

import zio.Task

trait DataDao {
  def getOne(id: BigDecimal): Task[Data]
}
