package com.clackjones.connectivitymap

class ReferenceProfileFileLoaderSpec extends UnitSpec {
  "splitLine" should "successfully parse a string containing a gene name and a rank into (String, Int) tuple" in {
    val geneRankString = "mygene   -1240"

    ReferenceProfileFileLoader.splitLine(geneRankString) shouldBe ("mygene", -1240)
  }

  it should "chop off any decimal point from the rank value" in {
    val geneRankString = "mygene   -1240.0"

    ReferenceProfileFileLoader.splitLine(geneRankString) shouldBe ("mygene", -1240)
  }
}
