package com.clackjones.connectivitymap

import java.io.File

import com.clackjones.connectivitymap.cmap.ConnectivityMapModule
import com.clackjones.connectivitymap.querysignature.{DefaultRandomSignatureGeneratorComponent, QuerySignatureFileLoaderComponent}
import com.clackjones.connectivitymap.referenceprofile._

object  Main extends ReferenceProfileFileLoaderComponent
                    with QuerySignatureFileLoaderComponent
                    with ReferenceSetCreatorByDrugDoseAndCellLineComponent
                    with ReferenceSetFileLoaderComponent
                    with DefaultRandomSignatureGeneratorComponent
                    with ConnectivityMapModule {

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
    val maxConnectionStrength = connectivityMap.maximumConnectionStrengthUnordered(totalNumberGenes, genesInQuery)

    val geneIds = refProfile.geneFoldChange.keys.toArray
    val sigLength = querySig.size

    val randomSignatureCount = 30000

    println("Running random signature generation")
    // generate random signatures
    val randomSignatures = List.range(0,randomSignatureCount).map {i =>
      randomSignatureGenerator.generateRandomSignature(geneIds, sigLength)
    }

    println("Create ReferenceSets")
    val refSets : Iterable[ReferenceSet] = referenceSetCreator.createReferenceSets(directory.getAbsolutePath(), directory.list())

    println("Calculating scores")

    val scores = refSets.par.map (refSet => {

      val profile : ReferenceProfile = referenceSetLoader.retrieveAverageReference(refSet)

      connectivityMap.calculateConnectionScore(profile, querySig, randomSignatures,
        maxConnectionStrength, refSet.filenames.size)
    })

    scores foreach { println(_) }

  }
}
