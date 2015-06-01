package com.clackjones.connectivitymap

/**
 * A class representing a ReferenceSet which contains a collection of
 * <code>filenames</code> representing the paths to each reference profile
 * file for this ReferenceSet
 *
 * The class also provides methods to load the ReferenceProfiles from this set
 * and also to instantiate a ReferenceProfile with the average fold change of
 * the different Probe IDs of each profile in the set.
 *
 * @param name The name to give this ReferenceSet
 * @param filenames A collection of absolute paths to the reference profiles of this ReferenceSet
 */
class ReferenceSet(val name: String, val filenames: Iterable[String]) {

  def retrieveReferenceProfiles() : Iterable[ReferenceProfile] = {
    throw new UnsupportedOperationException("retrieveReferenceProfiles not yet implemented")
  }

  /**
   * A function which loads all ReferenceProfiles from file in this ReferenceSet and
   * creates a ReferenceProfile which has the average (function specified by
   * user) fold change for each Probe ID
   * @param avgFun A function that describes how the average of each Gene ID is calculated (e.g. mean, median...etc.)
   * @return A ReferenceProfile with the average fold change for each gene ID from all reference profiles int the set
   */
  def retrieveAverageReferenceProfile(avgFun: Set[Float] => Float) : ReferenceProfile = {
    throw new UnsupportedOperationException("retrieveAverageReferenceProfile not yet implemented")
  }
}