package com.clackjones.connectivitymap.referenceprofile


trait ReferenceSetLoaderComponent {
  def referenceSetLoader : ReferenceSetLoader

  /**
   * A class to take a [[ReferenceSet]] object and load all of its
   * constituent [[ReferenceProfile]]s from file.
   */
  trait ReferenceSetLoader {

    /**
     * load all [[ReferenceProfile]]s associated with a
     * [[ReferenceSet]]
     * @param set the [[ReferenceSet]]
     * @return A set of [[ReferenceProfile]] objects
     */
    def retrieveAllProfiles(set: ReferenceSet): Set[ReferenceProfile]

    /**
     * creates a [[ReferenceProfile]] whose fold-change for each Probe ID
     * is the average of the fold change for each Probe ID of each
     * ReferenceProfile in this [[ReferenceSet]].
     * @param set the [[ReferenceSet]]
     * @return a [[ReferenceProfile]] with average fold changes of this set
     */
    def retrieveAverageReference(set: ReferenceSet): ReferenceProfile
  }
}


trait ReferenceSetFileLoaderComponent extends ReferenceSetLoaderComponent {
  this: ReferenceProfileLoaderComponent =>
  val referenceSetLoader = new ReferenceSetFileLoader

  /**
   * A [[ReferenceSetLoader]] that loads uses a
   * [[ReferenceProfileLoaderComponent]] to retrieve all the
   * [[ReferenceProfile]]s of a [[ReferenceSet]]
   */
  class ReferenceSetFileLoader extends ReferenceSetLoader {

    def retrieveAllProfiles(set: ReferenceSet): Set[ReferenceProfile] = {
      (set.filenames map (referenceProfileLoader.loadReferenceProfile(_))).toSet
    }

    def retrieveAverageReference(set: ReferenceSet): ReferenceProfile = {
      throw new UnsupportedOperationException("retrieveAverageReference not yet implemented")
    }
  }

}
