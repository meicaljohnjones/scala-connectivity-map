package com.clackjones.connectivitymap

class ConnectivityMapSpec extends UnitSpec {
  "A single gene expression profile and query signature" should "correctly calculate a single connection score" in {
    val expressionProfile : Map[String, Int] = Map("gene1" -> 1, "gene2" -> -5, "gene3" -> 7)
    val querySignature : Map[String, Int] = Map("gene2" -> 1, "gene3" -> 1)

    val strength = ConnectivityMap.connectionStrength(expressionProfile, querySignature)

    strength shouldBe 2
  }
}