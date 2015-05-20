package com.clackjones.connectivitymap
import ConnectivityMap._

class ConnectivityMapSpec extends UnitSpec {
  "A single gene expression profile and query signature" should "correctly calculate a single connection score" in {
    val expressionProfile = new ReferenceProfile(name = "profile1", Map("gene1" -> 1, "gene2" -> -5, "gene3" -> 7))
    val querySignature : Map[String, Int] = Map("gene2" -> 1, "gene3" -> 1)

    connectionStrength(expressionProfile, querySignature) shouldBe (expressionProfile.name, 2)
  }

  "Passing a single gene expression profile and query signature to connectionScores" should
    "return one connection strength tuple with score 1.0" in {

    val expressionProfile = new ReferenceProfile(name = "profile1", Map("gene1" -> 1, "gene2" -> -5, "gene3" -> 7))
    val querySignature : Map[String, Int] = Map("gene2" -> 1, "gene3" -> 1)

    def mockConnectionStrength(prof: ReferenceProfile, query: Map[String, Int]): (String, Int) = ("resultprofile", 50)

    connectionScores(Set(expressionProfile), querySignature, mockConnectionStrength) shouldBe Set(("resultprofile", 1f))
  }

}