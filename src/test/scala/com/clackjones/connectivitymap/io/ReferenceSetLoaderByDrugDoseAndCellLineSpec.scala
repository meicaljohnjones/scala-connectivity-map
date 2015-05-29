package com.clackjones.connectivitymap.io

import com.clackjones.connectivitymap.ReferenceSet
import com.clackjones.connectivitymap.UnitSpec

class ReferenceSetLoaderByDrugDoseAndCellLineSpec extends UnitSpec {
  "createReferenceSets" should "return an Iterator with one ReferenceSet in it " +
    "for valid single filename" in {

    val path = "this/is/my/path"
    val experimentId = 7213
    val filenames = List(
      "cinpocetine_11.4muM_MCF7_" + experimentId + ".ref.tab"
    )

    val result = ReferenceSetLoaderByDrugDoseAndCellLine.createReferenceSets(path, filenames)

    result.size shouldEqual 1
    result.head.filenames.size shouldEqual 1
    val filename = result.head.filenames.head
    val setName = result.head.name

    filename shouldEqual path + "/" + filenames(0)
    setName shouldEqual "cinpocetine_11.4muM_MCF7"
  }

  // TODO write tests to check I'm correctly partition up reference sets
}
