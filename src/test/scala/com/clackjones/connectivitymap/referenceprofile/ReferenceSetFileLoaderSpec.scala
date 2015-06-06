package com.clackjones.connectivitymap.referenceprofile

import com.clackjones.connectivitymap.UnitSpec

class ReferenceSetFileLoaderSpec extends UnitSpec {

  trait MockReferenceProfileLoaderComponent extends ReferenceProfileLoaderComponent {
    val referenceProfileLoader = new MockReferenceProfileLoader

    class MockReferenceProfileLoader extends ReferenceProfileLoader {

      val referenceProfiles : Map[String, ReferenceProfile] = {
        Map(

          "/path/to/profile1" -> new ReferenceProfile("profile1",
            Map("gene1" -> 2, "gene2" -> 1, "gene3" -> 3)),

          "/path/to/profile2" -> new ReferenceProfile("profile2",
            Map("gene1" -> -1, "gene2" -> 2, "gene3" -> 2)),

          "/path/to/profile3" -> new ReferenceProfile("profile3",
            Map("gene1" -> 0, "gene2" -> 1, "gene3" -> -1))
        )
      }

      override def loadReferenceProfile(path: String): ReferenceProfile = {
        referenceProfiles(path)
      }
    }
  }
  val referenceSetLoader = (new ReferenceSetFileLoaderComponent with MockReferenceProfileLoaderComponent).referenceSetLoader

  "ReferenceSetFileLoader" should "load a set of ReferenceProfiles from a ReferenceSet" in {

    val refSet = new ReferenceSet("myrefset", Set("/path/to/profile1", "/path/to/profile2", "/path/to/profile3"))
    val result : Set[ReferenceProfile] = referenceSetLoader.retrieveAllProfiles(refSet)

    result.size shouldEqual 3
    val resultList = result.toList

    resultList(0).name shouldBe "profile1"
    resultList(1).name shouldBe "profile2"
    resultList(2).name shouldBe "profile3"

    resultList(0).geneFoldChange("gene1") shouldBe 2
    resultList(1).geneFoldChange("gene1") shouldBe -1
    resultList(2).geneFoldChange("gene1") shouldBe 0

    resultList(0).geneFoldChange("gene2") shouldBe 1
    resultList(1).geneFoldChange("gene2") shouldBe 2
    resultList(2).geneFoldChange("gene2") shouldBe 1

    resultList(0).geneFoldChange("gene3") shouldBe 3
    resultList(1).geneFoldChange("gene3") shouldBe 2
    resultList(2).geneFoldChange("gene3") shouldBe -1

  }

  it should "return one ReferenceProfile object when finding the average fold change of all profiles in this ReferenceSet" in {
    val refSet = new ReferenceSet("myrefset", Set("/path/to/profile1", "/path/to/profile2", "/path/to/profile3"))
    val result : ReferenceProfile = referenceSetLoader.retrieveAverageReference(refSet)

    val geneFoldMap = result.geneFoldChange
    geneFoldMap.size shouldEqual 3

    geneFoldMap("gene1") shouldEqual (2 - 1 + 0) / 3f
    geneFoldMap("gene2") shouldEqual (1 + 2 + 1) / 3f
    geneFoldMap("gene3") shouldEqual (3 + 2 - 1) / 3f
  }

}
