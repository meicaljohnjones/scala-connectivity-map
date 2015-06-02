package com.clackjones.connectivitymap.io

import com.clackjones.connectivitymap.ReferenceSet

trait ReferenceSetCreator {
  def createReferenceSets(pathToFiles: String, filenames: Iterable[String]): Iterable[ReferenceSet]
}

/**
 * A ReferenceSetCreator that takes a list of filenames containing reference profile data
 * and splits them according by experiment ID such that they are grouped by
 * perturbagen, dose and cell-line.
 * <code>[perturbagen]_[dose]_[cell-line]_[experiment-id].ref.tab</code>
 */
object ReferenceSetCreatorByDrugDoseAndCellLine extends ReferenceSetCreator {
  override def createReferenceSets(pathToFiles: String, filenames: Iterable[String]): Iterable[ReferenceSet] = {

    val path = if(!pathToFiles.endsWith("/")) pathToFiles + "/" else pathToFiles
    val absolutePathToFilesAndReferenceSetNames: Iterable[(String, String)] = filenames map (f => {
      (filenameToReferenceSetName(f), path + f)
    })

    val referenceSetMap = absolutePathToFilesAndReferenceSetNames.groupBy(_._1).mapValues(_.map(_._2))

    val referenceSets: Iterable[ReferenceSet] = referenceSetMap.keys map (setName => {
      val setFilenames = referenceSetMap(setName)
      new ReferenceSet(setName, setFilenames.toSet)
    })

    referenceSets
  }

  private def filenameToReferenceSetName(filename: String) : String = filename.substring(0, filename.lastIndexOf("_"))
}