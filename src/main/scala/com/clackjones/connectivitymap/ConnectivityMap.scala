package com.clackjones.connectivitymap

object ConnectivityMap {

  def connectionStrength(referenceProfile: Map[String, Int], querySignature: Map[String, Int]): Int = {
    val keys = querySignature.keys.toSet

    val filteredReferenceProfile = referenceProfile filterKeys (refKey => keys contains refKey)

    val strengths = filteredReferenceProfile map {
      case (key, value) => {
        querySignature get key match {
          case Some(rank) => value * rank
        }
      }
    }

    strengths.toList.sum
  }

}
