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

  it should "return an Iterator with one ReferenceSet in it for two file names with different experiment IDs but " +
    "with the same drug, dose and cell line" in  {

    val path = "this/is/my/path"
    val experimentId = 7213
    val otherExperimentId = 7214
    val filenames = List(
      "cinpocetine_11.4muM_MCF7_" + experimentId + ".ref.tab",
      "cinpocetine_11.4muM_MCF7_" + otherExperimentId + ".ref.tab"
    )

    val result: Iterable[ReferenceSet] = ReferenceSetLoaderByDrugDoseAndCellLine.createReferenceSets(path, filenames)

    val resultIt = result.toIterator
    result.size shouldEqual 1

    val refSet = resultIt.next()
    refSet.filenames.toList(0) shouldEqual path + "/" + filenames(0)
    refSet.filenames.toList(1) shouldEqual path + "/" + filenames(1)
    refSet.name shouldEqual "cinpocetine_11.4muM_MCF7"
  }

  it should "return an Iterator with two ReferenceSet in it for two file names with different drugs" in {
    fail("not yet implemented")
  }

  it should "return an Iterator with two ReferenceSet in it for two file names with different doses" in {
    fail("not yet implemented")
  }

  it should "return an Iterator with two ReferenceSet in it for two file names with different cell lines" in {
    fail("not yet implemented")
  }
}
