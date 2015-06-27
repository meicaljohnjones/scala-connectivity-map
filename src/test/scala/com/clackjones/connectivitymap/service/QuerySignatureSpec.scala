package com.clackjones.connectivitymap.service

import com.clackjones.connectivitymap.UnitSpec

class QuerySignatureSpec extends UnitSpec {

  "isOrderedSignature" should "return false if a QuerySignature's geneUpDown contains only the values 1, 0 or -1" in {
    val sig = QuerySignature("Example Sig", Map("gen1" -> 1, "gen2" -> 0, "gen3" -> -1))

    sig.isOrderedSignature shouldEqual false
  }
  
  it should "return true if a QuerySignature's geneUpDown contains values other than 1, 0 or -1" in {
    val sig = QuerySignature("Example Sig", Map("gen1" -> 1, "gen2" -> 0, "gen3" -> 2))

    sig.isOrderedSignature shouldEqual true
  }
}
