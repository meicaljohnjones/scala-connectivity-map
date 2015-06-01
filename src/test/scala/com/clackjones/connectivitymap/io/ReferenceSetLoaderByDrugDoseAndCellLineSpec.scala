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
    val path = "this/is/my/path"
    val experimentId = 7213
    val filenames = List(
      "cinpocetine_11.4muM_MCF7_" + experimentId + ".ref.tab",
      "winpocetine_11.4muM_MCF7_" + experimentId + ".ref.tab"
    )

    val result: Iterable[ReferenceSet] = ReferenceSetLoaderByDrugDoseAndCellLine.createReferenceSets(path, filenames)

    result.size shouldEqual 2

    val resultsList = result.toList

    val refSet1 = resultsList.find(refset => refset.name.equals("cinpocetine_11.4muM_MCF7"))
    refSet1 should not be empty

    val refSet2 = resultsList.find(refset => refset.name.equals("winpocetine_11.4muM_MCF7"))
    refSet2 should not be empty

    refSet1.get.filenames.toList(0) shouldEqual path + "/" + filenames(0)
    refSet2.get.filenames.toList(0) shouldEqual path + "/" + filenames(1)
  }

  it should "return an Iterator with two ReferenceSet in it for two file names with different doses" in {
    val path = "this/is/my/path"
    val experimentId = 7213
    val filenames = List(
      "cinpocetine_11.4muM_MCF7_" + experimentId + ".ref.tab",
      "cinpocetine_12.4muM_MCF7_" + experimentId + ".ref.tab"
    )

    val result: Iterable[ReferenceSet] = ReferenceSetLoaderByDrugDoseAndCellLine.createReferenceSets(path, filenames)

    result.size shouldEqual 2

    val resultsList = result.toList

    val refSet1 = resultsList.find(refset => refset.name.equals("cinpocetine_11.4muM_MCF7"))
    refSet1 should not be empty

    val refSet2 = resultsList.find(refset => refset.name.equals("cinpocetine_12.4muM_MCF7"))
    refSet2 should not be empty

    refSet1.get.filenames.toList(0) shouldEqual path + "/" + filenames(0)
    refSet2.get.filenames.toList(0) shouldEqual path + "/" + filenames(1)
  }

  it should "return an Iterator with two ReferenceSet in it for two file names with different cell lines" in {
    val path = "this/is/my/path"
    val experimentId = 7213
    val filenames = List(
      "cinpocetine_11.4muM_MCF7_" + experimentId + ".ref.tab",
      "cinpocetine_11.4muM_MCF8_" + experimentId + ".ref.tab"
    )

    val result: Iterable[ReferenceSet] = ReferenceSetLoaderByDrugDoseAndCellLine.createReferenceSets(path, filenames)

    result.size shouldEqual 2

    val resultsList = result.toList

    val refSet1 = resultsList.find(refset => refset.name.equals("cinpocetine_11.4muM_MCF7"))
    refSet1 should not be empty

    val refSet2 = resultsList.find(refset => refset.name.equals("cinpocetine_11.4muM_MCF8"))
    refSet2 should not be empty

    refSet1.get.filenames.toList(0) shouldEqual path + "/" + filenames(0)
    refSet2.get.filenames.toList(0) shouldEqual path + "/" + filenames(1)
  }
}
