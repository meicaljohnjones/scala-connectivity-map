package com.clackjones.connectivitymap
import ConnectivityMap._
import com.clackjones.connectivitymap.referenceprofile.ReferenceProfile

class ConnectivityMapSpec extends UnitSpec {
  "A single gene expression profile and query signature" should "correctly calculate a single connection score" in {
    val expressionProfile = new ReferenceProfile(name = "profile1", Map("gene1" -> 1, "gene2" -> -5, "gene3" -> 7))
    val querySignature : Map[String, Int] = Map("gene2" -> 1, "gene3" -> 1)

    connectionStrength(expressionProfile, querySignature) shouldBe (expressionProfile.name, 2)
  }

  "connectionScore" should "return a connection strength tuple with score 1.0 when maxConnectionStrength has the same strength value" in {

    val expressionProfile = new ReferenceProfile(name = "profile1", Map("gene1" -> 1, "gene2" -> -5, "gene3" -> 7))
    val querySignature : Map[String, Int] = Map("gene2" -> 1, "gene3" -> 1)

    val strength = 50

    def mockConnectionStrength(prof: ReferenceProfile, query: Map[String, Int]): (String, Int) =
      ("resultprofile", strength)

    val maxConnectionStrength: Int = strength

    connectionScore(expressionProfile, querySignature,
      mockConnectionStrength, maxConnectionStrength) shouldBe ("resultprofile", 1f)
  }

  it should "return one connection strength tuple with score 0.5 when maxConnectionStrength has twice strength value" in {

    val expressionProfile = new ReferenceProfile(name = "profile1", Map("gene1" -> 1, "gene2" -> -5, "gene3" -> 7))
    val querySignature : Map[String, Int] = Map("gene2" -> 1, "gene3" -> 1)

    val strength = 50

    def mockConnectionStrength(prof: ReferenceProfile, query: Map[String, Int]): (String, Int) =
      ("resultprofile", strength)

    val maxConnectionStrength: Int = strength * 2

    connectionScore(expressionProfile, querySignature,
      mockConnectionStrength, maxConnectionStrength) shouldBe ("resultprofile", 0.5f)
  }

  "generateRandomSignature" should "generate a random signature from a list of gene IDs with values (-1, 1)" in {
    val geneIds: Array[String] = (1 to 10 map (i => "gene_"+i)).toArray
    println(geneIds)
    val signatureLength = 5

    def mockNextRandomGeneIndex = (for {i <- List(0,2,4,6,8) } yield i ).toIterator
    def mockNextRandomUpDown = (for {i <- List(1,-1,-1,-1,1) } yield i ).toIterator

    val result = generateRandomSignature(geneIds, signatureLength,
      mockNextRandomGeneIndex.next, mockNextRandomUpDown.next)

    println(result)

    result.size shouldBe signatureLength

    result get "gene_1" shouldBe Some(1)
    result get "gene_3" shouldBe Some(-1)
    result get "gene_5" shouldBe Some(-1)
    result get "gene_7" shouldBe Some(-1)
    result get "gene_9" shouldBe Some(1)
  }
}