package com.clackjones.connectivitymap

/**
 * A class representing a ReferenceSet which contains a collection of
 * <code>filenames</code> representing the paths to each reference profile
 * file for this ReferenceSet
 *
 * @param name The name to give this ReferenceSet
 * @param filenames A collection of absolute paths to the reference profiles of this ReferenceSet
 */
class ReferenceSet(val name: String, val filenames: Iterable[String])