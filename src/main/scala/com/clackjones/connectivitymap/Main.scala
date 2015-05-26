package com.clackjones.connectivitymap
import java.io.File
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.collection.parallel.immutable.ParHashMap
import scala.util.Random

object  Main {

  def main(args: Array[String]): Unit = {

    //list all the files
    val directory = new File("/home/mike/workspace/sscmap-in-r/reffiles")
    val files = directory.list() map (filename => directory.getAbsolutePath + "/" + filename)

    val querySig = QuerySignatureFileLoader.loadQuerySignature("/home/mike/workspace/sscmap-in-r/queries/Estrogen.sig")
    val refProfile = ReferenceProfileFileLoader.loadReferenceProfile(files(0))

    // calculate the max score
    val totalNumberGenes = refProfile.geneFoldChange.size
    val genesInQuery = querySig.size
    val maxConnectionStrength = ConnectivityMap.maximumConnectionStrengthUnordered(totalNumberGenes, genesInQuery)

    val geneIds = refProfile.geneFoldChange.keys.toArray
    val geneIdCount = geneIds.size
    val sigLength = querySig.size


    // generate random signatures
    val randomSignatures = (List.range(0,10000).par.map {i =>
      val randomNumberGen = new Random()
      def getRandomGeneIndex(): Int = randomNumberGen.nextInt(geneIdCount)
      def getRandomUpDown(): Int = if (randomNumberGen.nextInt(1) != 1) -1 else 1

      ConnectivityMap.generateRandomSignature(geneIds, sigLength,
        getRandomGeneIndex, getRandomUpDown)
    }).par


    val fileNamesBuffer: ArrayBuffer[String] = ArrayBuffer() ++ files

    val scores = fileNamesBuffer .par.map (path => {
      val profile = ReferenceProfileFileLoader.loadReferenceProfile(path)

      val trueScore = ConnectivityMap.connectionScore(profile, querySig, ConnectivityMap.connectionStrength,
        maxConnectionStrength)

      val randomScores = randomSignatures.par.map {sig =>
        ConnectivityMap.connectionScore(profile, sig, ConnectivityMap.connectionStrength,
          maxConnectionStrength)
      }

      trueScore
    })

  }
}
