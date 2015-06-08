package com.clackjones.connectivitymap

import java.io.File
import scala.collection.mutable.ArrayBuffer
import scala.util.Random

import com.clackjones.connectivitymap.querysignature.{DefaultRandomSignatureGeneratorComponent, QuerySignatureFileLoaderComponent}
import com.clackjones.connectivitymap.referenceprofile.{ReferenceSetFileLoaderComponent, ReferenceSet, ReferenceSetCreatorByDrugDoseAndCellLineComponent, ReferenceProfileFileLoaderComponent}

object  Main extends ReferenceProfileFileLoaderComponent
                    with QuerySignatureFileLoaderComponent
                    with ReferenceSetCreatorByDrugDoseAndCellLineComponent
                    with ReferenceSetFileLoaderComponent
                    with DefaultRandomSignatureGeneratorComponent {

  def main(args: Array[String]): Unit = {

    //list all the files

    val directory = new File(getClass().getResource("/reffiles_subset").toURI())
    val files = directory.list() map (filename => directory.getAbsolutePath + "/" + filename)

    val estrogenSignature = new File(getClass().getResource("/queries/Estrogen.sig").toURI())
    val querySig = querySignatureLoader.loadQuerySignature(estrogenSignature.getAbsolutePath())
    val refProfile = referenceProfileLoader.loadReferenceProfile(files(0))

    // calculate the max score
    val totalNumberGenes = refProfile.geneFoldChange.size
    val genesInQuery = querySig.size
    val maxConnectionStrength = ConnectivityMap.maximumConnectionStrengthUnordered(totalNumberGenes, genesInQuery)

    val geneIds = refProfile.geneFoldChange.keys.toArray
    val sigLength = querySig.size

    val randomSignatureCount = 30000

    println("Running random signature generation")
    // generate random signatures
    val randomSignatures = List.range(0,randomSignatureCount).par.map {i =>
      randomSignatureGenerator.generateRandomSignature(geneIds, sigLength)
    }.par

    println("Create ReferenceSets")
    val refSets : Iterable[ReferenceSet] = referenceSetCreator.createReferenceSets(directory.getAbsolutePath(), directory.list())

    println("Calculating scores")

    val scores = refSets.par.map (refSet => {

      val profile = referenceSetLoader.retrieveAverageReference(refSet)

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
