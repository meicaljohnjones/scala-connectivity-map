package com.clackjones.connectivitymap
import java.io.File

object Main {

  def main(args: Array[String]): Unit = {

    //list all the files
    val directory = new File("/home/mike/workspace/sscmap-in-r/reffiles")
    val files = directory.list() map (filename => directory.getAbsolutePath+"/"+filename)

    val querySig = QuerySignatureFileLoader.loadQuerySignature("/home/mike/workspace/sscmap-in-r/queries/Estrogen.sig")
    val refProfile = ReferenceProfileFileLoader.loadReferenceProfile("/home/mike/Desktop/reffile.tab")

    // calculate the max score
    val totalNumberGenes = refProfile.geneFoldChange.size
    val genesInQuery = querySig.size
    val maxConnectionStrength = ConnectivityMap.maximumConnectionStrengthUnordered(totalNumberGenes, genesInQuery)


    println("Start stuff")
    // TODO for each file in files

    val scores = files.toList.map (path => {
      val profile = ReferenceProfileFileLoader.loadReferenceProfile(path)

      val connectionStrength = ConnectivityMap.connectionScore(profile, querySig, ConnectivityMap.connectionStrength,
        maxConnectionStrength)

      connectionStrength
    })

    //scores foreach (println(_))
  }

}
