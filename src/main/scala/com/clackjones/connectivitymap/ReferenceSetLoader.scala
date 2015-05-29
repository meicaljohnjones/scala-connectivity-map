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
    throw new UnsupportedOperationException("createReferenceSets not yet implemented")
  }
}