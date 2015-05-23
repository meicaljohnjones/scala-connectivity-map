package com.clackjones.connectivitymap

object Main {

  def main(args: Array[String]): Unit = {

    //load the query signature and the reference profile
    val querySig = Map("201291_s_at" ->	1, "201292_at" ->	1, "201508_at" ->	1,
      "202434_s_at" ->	1, "202435_s_at" ->	1, "202436_s_at" ->	1,
      "202437_s_at" ->	1, "202599_s_at" -> 1)
    val refProfile = ReferenceProfileFileLoader.loadReferenceProfile("/home/mike/Desktop/reffile.tab")

    // calculate the max score
    val totalNumberGenes = refProfile.geneFoldChange.size
    val genesInQuery = querySig.size
    val maxConnectionStrength = ConnectivityMap.maximumConnectionStrengthUnordered(totalNumberGenes, genesInQuery)

    // calculate the connection score
    val connectionScore = ConnectivityMap.connectionScore(refProfile, querySig, ConnectivityMap.connectionStrength,
      maxConnectionStrength)

    println(connectionScore)
  }

}
