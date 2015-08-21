package com.clackjones.connectivitymap.spark

import com.clackjones.connectivitymap.UnitSpec
import scala.util.{Failure, Success, Try}

class SparkCmapHelperFunctionsSpec extends UnitSpec {

  "filenameToRefsetName" should "return a Success with the ReferenceSet name generated from the filename" in {
    val path = "/path/to/filename/"
    val filename =
      path + "BRD-A00546892__BIPERIDEN__trt_cp__10.0um__A375__6H__CPC004_A375_6H_X1_B3_DUO52HI53LO:J19.ref.tab.gz"

    val result : Try[String] = SparkCmapHelperFunctions.filenameToRefsetName(filename)

    result shouldBe a [Success[_]]
    result.get shouldEqual "BRD-A00546892__A375"
  }

  it should "return a Failure object with the original filename string in it" in {
    val path = "/path/to/filename/"
    val filename =
      path + "Invalid_filename"

    val result : Try[String] = SparkCmapHelperFunctions.filenameToRefsetName(filename)
    result shouldBe a [Failure[_]]
  }
}
