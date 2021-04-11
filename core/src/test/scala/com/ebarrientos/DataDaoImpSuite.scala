package com.ebarrientos

import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class DataDaoImpSuite extends AnyFunSuite with ScalaCheckPropertyChecks {
  lazy val dao = new DataDaoImp()

  test("getSingleItem") {
    forAll { (n: Int) =>
      val data = zio.Runtime.default.unsafeRun(dao.getOne(BigDecimal(n)))
      assert(data.id == BigDecimal(n))
      assert(!data.name.isEmpty())
      assert(!data.address.street.isEmpty())
    }
  }
}
