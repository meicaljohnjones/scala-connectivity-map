package com.clackjones.connectivitymap
import ConnectivityMap._

class ConnectivityMapSpec extends UnitSpec {
  "A single gene expression profile and query signature" should "correctly calculate a single connection score" in {
    val expressionProfile = new ReferenceProfile(name = "profile1", Map("gene1" -> 1, "gene2" -> -5, "gene3" -> 7))
    val querySignature : Map[String, Int] = Map("gene2" -> 1, "gene3" -> 1)

    connectionStrength(expressionProfile, querySignature) shouldBe (expressionProfile.name, 2)
  }
}