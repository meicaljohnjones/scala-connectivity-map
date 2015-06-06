package com.clackjones.connectivitymap.referenceprofile

/**
 * A class containing a description of a reference profile
 * which will contain both the name of the profile as well as
 * the mapping between probe_ids (the names of genes) and the
 * fold change in their expression.
 */
class ReferenceProfile(val name: String, val geneFoldChange: Map[String, Float]) {

  override def toString(): String = s"ReferenceProfile: $name\n"
}
