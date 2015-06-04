package com.clackjones.connectivitymap.referenceprofile

import com.clackjones.connectivitymap.UnitSpec

class ReferenceProfileSpec extends UnitSpec {
  "toString" should "be of the format 'ReferenceProfile: name'" in {
    val expressionProfile = new ReferenceProfile(name = "profile1", Map("gene1" -> 1, "gene2" -> -5, "gene3" -> 7))
    val name: String = expressionProfile.name

    expressionProfile.toString() shouldBe s"ReferenceProfile: $name\n"
  }
}
