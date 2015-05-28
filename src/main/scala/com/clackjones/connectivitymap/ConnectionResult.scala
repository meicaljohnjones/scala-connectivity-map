package com.clackjones.connectivitymap

/**
 * A class that simply stores the result for a particular connection score
 * for a single ReferenceProfile
 */
class ConnectionResult(referenceProfileNames: Iterable[String], connectionScore: Float, pVal: Float) {
  override def toString(): String = {
    val profileNames = referenceProfileNames.mkString("\n")
    List("ConnectionResult:",profileNames,
      "Score: "+connectionScore.toString,
      "P-value: "+pVal.toString).mkString("\n")
  }
}