package com.clackjones.connectivitymap

object ConnectivityMap {

  def connectionStrength(referenceProfile: ReferenceProfile, querySignature: Map[String, Int]): (String, Int) = {
    val keys = querySignature.keys.toSet

    val filteredReferenceProfile = referenceProfile.geneFoldChange filterKeys (refKey => keys contains refKey)

    val strengths = filteredReferenceProfile map {
      case (key, value) => {
        querySignature get key match {
          case Some(rank) => value * rank
        }
      }
    }

    (referenceProfile.name, strengths.toList.sum)
  }



}
