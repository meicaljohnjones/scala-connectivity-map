package com.clackjones.connectivitymap
import java.io.File

object Main {

  def main(args: Array[String]): Unit = {

    //list all the files
    val directory = new File("/home/mike/workspace/sscmap-in-r/reffiles")
    val files = directory.list() map (filename => directory.getAbsolutePath+"/"+filename)


    //load the query signature and the reference profile
    val querySig = Map("201291_s_at" ->	1, "201292_at" ->	1, "201508_at" ->	1,
      "202434_s_at" ->	1, "202435_s_at" ->	1, "202436_s_at" ->	1,
      "202437_s_at" ->	1, "202599_s_at" -> 1)
    val refProfile = ReferenceProfileFileLoader.loadReferenceProfile("/home/mike/Desktop/reffile.tab")

    // calculate the max score
    val totalNumberGenes = refProfile.geneFoldChange.size
    val genesInQuery = querySig.size
    val maxConnectionStrength = ConnectivityMap.maximumConnectionStrengthUnordered(totalNumberGenes, genesInQuery)


    println("Start stuff")
    // TODO for each file in files

    val startTime = System.nanoTime()

    val scores = files.toList.map (path => {
      val profile = ReferenceProfileFileLoader.loadReferenceProfile(path)

      val connectionStrength = ConnectivityMap.connectionScore(profile, querySig, ConnectivityMap.connectionStrength,
        maxConnectionStrength)

      connectionStrength
    })

    val endTime = System.nanoTime()

    println("Done. Time elapsed: "+ (endTime - startTime) + "ns")
    //scores foreach (println(_))
  }

}
