package com.clackjones.connectivitymap
import java.io.File
import scala.collection.mutable.{ArrayBuffer, ListBuffer}
import scala.collection.parallel.immutable.ParHashMap
import scala.util.Random

object  Main {

  def main(args: Array[String]): Unit = {

    //list all the files

    val directory = new File(getClass().getResource("/reffiles_subset").toURI())
    val files = directory.list() map (filename => directory.getAbsolutePath + "/" + filename)

    val estrogenSignature = new File(getClass().getResource("/queries/Estrogen.sig").toURI())
    val querySig = QuerySignatureFileLoader.loadQuerySignature(estrogenSignature.getAbsolutePath())
    val refProfile = ReferenceProfileFileLoader.loadReferenceProfile(files(0))

    // calculate the max score
    val totalNumberGenes = refProfile.geneFoldChange.size
    val genesInQuery = querySig.size
    val maxConnectionStrength = ConnectivityMap.maximumConnectionStrengthUnordered(totalNumberGenes, genesInQuery)

    val geneIds = refProfile.geneFoldChange.keys.toArray
    val geneIdCount = geneIds.size
    val sigLength = querySig.size

    val randomSignatureCount = 10000

    println("Running random signature generation")
    // generate random signatures
    val randomSignatures = List.range(0,randomSignatureCount).par.map {i =>
      val randomNumberGen = new Random()
      def getRandomGeneIndex(): Int = randomNumberGen.nextInt(geneIdCount)
      def getRandomUpDown(): Int = if (randomNumberGen.nextInt(1) != 1) -1 else 1

      ConnectivityMap.generateRandomSignature(geneIds, sigLength,
        getRandomGeneIndex, getRandomUpDown)
    }.par

    println("Calculating scores")

    val fileNamesBuffer: ArrayBuffer[String] = ArrayBuffer() ++ files

    val scores = fileNamesBuffer .par.map (path => {
      val profile = ReferenceProfileFileLoader.loadReferenceProfile(path)

      val trueScoreTuple = ConnectivityMap.connectionScore(profile, querySig, ConnectivityMap.connectionStrength,
        maxConnectionStrength)

      val randomScores = randomSignatures.par.map {sig =>
        ConnectivityMap.connectionScore(profile, sig, ConnectivityMap.connectionStrength,
          maxConnectionStrength)
      }

      val trueScore = trueScoreTuple._2
      val pVal = randomScores.foldLeft(0f)((count, randScoreTuple) => {
        val randScore = randScoreTuple._2
        if (randScore >= trueScore) count + 1 else count
      }) / randomSignatureCount

      new ConnectionResult(Set(trueScoreTuple._1), trueScore, pVal)
    })

    scores foreach { println(_) }

  }
}
