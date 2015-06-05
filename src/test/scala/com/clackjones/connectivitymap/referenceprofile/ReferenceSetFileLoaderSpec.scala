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

  "ReferenceSetFileLoader" should "load a set of ReferenceProfiles from a ReferenceSet" in {
    val referenceSetLoader = (new ReferenceSetFileLoaderComponent with MockReferenceProfileLoaderComponent).referenceSetLoader

    val refSet = new ReferenceSet("myrefset", Set("/path/to/profile1", "/path/to/profile2", "/path/to/profile3"))

    val result = referenceSetLoader.retrieveAllProfiles(refSet)

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

}
