package com.clackjones.connectivitymap.io

import com.clackjones.connectivitymap.ReferenceSet

trait ReferenceSetLoader {
  def createReferenceSets(pathToFiles: String, filenames: Iterable[String]): Iterable[ReferenceSet]
}

/**
 * A ReferenceSetLoader that takes a list of filenames containing reference profile data
 * and splits them according by experiment ID such that they are grouped by
 * perturbagen, dose and cell-line.
 * <code>[perturbagen]_[dose]_[cell-line]_[experiment-id].ref.tab</code>
 */
object ReferenceSetLoaderByDrugDoseAndCellLine extends ReferenceSetLoader {
  override def createReferenceSets(pathToFiles: String, filenames: Iterable[String]): Iterable[ReferenceSet] = {

    val path = if(!pathToFiles.endsWith("/")) pathToFiles + "/" else pathToFiles
    val absolutePathToFilesAndReferenceSetNames: Iterable[(String, String)] = filenames map (f => {
      (filenameToReferenceSetName(f), path + f)
    })

    //TODO group tuples

    absolutePathToFilesAndReferenceSetNames map (absPathFilename =>
      new ReferenceSet(absPathFilename._1, Set(absPathFilename._2)))
  }

  private def filenameToReferenceSetName(filename: String) : String = filename.substring(0, filename.lastIndexOf("_"))
}