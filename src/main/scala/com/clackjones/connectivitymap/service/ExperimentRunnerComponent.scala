package com.clackjones.connectivitymap.service

import com.clackjones.connectivitymap.QuerySignatureMap
import com.clackjones.connectivitymap.querysignature.RandomSignatureGeneratorComponent
import com.clackjones.connectivitymap.cmap.ConnectivityMapModule
import com.clackjones.connectivitymap.referenceprofile.ReferenceSetLoaderComponent

trait ExperimentRunnerComponent {
  type ConnectivityMapReferenceSet = com.clackjones.connectivitymap.referenceprofile.ReferenceSet
  type ConnectivityMapReferenceProfile = com.clackjones.connectivitymap.referenceprofile.ReferenceProfile
  type ConnectivityMapConnectionScoreResult = com.clackjones.connectivitymap.cmap.ConnectionScoreResult

  def experimentRunner : ExperimentRunner

  trait ExperimentRunner {
    def runExperimentUnorderedConnectionScore(experiment: Experiment) : Option[ExperimentResult]
    def runExperimentOrderedConnectionScore (experiment: Experiment) : Option[ExperimentResult]
  }
}

trait DefaultExperimentRunnerComponent extends ExperimentRunnerComponent {
  this: RandomSignatureGeneratorComponent with ConnectivityMapModule
    with ReferenceSetLoaderComponent with ReferenceSetProviderComponent
    with QuerySignatureProviderComponent =>
  val experimentRunner = new DefaultExperimentRunner

  class DefaultExperimentRunner extends ExperimentRunner {

    def runExperimentUnorderedConnectionScore(experiment: Experiment) : Option[ExperimentResult] = {
      val refsets : Set[ReferenceSet] = referenceSetProvider.findAll().toSet

      val referenceSets : Set[ConnectivityMapReferenceSet] = refsets map {
        s => new ConnectivityMapReferenceSet(s.name, s.filenames)
      }

      val serviceQuerySignature : Option[QuerySignature] = querySignatureProvider.find(experiment.querySignatureId)
      if (!serviceQuerySignature.isDefined) {
        return None
      }

      val querySignature : QuerySignatureMap = serviceQuerySignature.get.geneUpDown

      val firstRefSet : ConnectivityMapReferenceSet = referenceSets.toIterator.next()
      val setProfiles : Set[ConnectivityMapReferenceProfile] = referenceSetLoader.retrieveAllProfiles(firstRefSet)
      val firstProfile : ConnectivityMapReferenceProfile = setProfiles.toIterator.next()

      val geneIds : Array[String] = firstProfile.geneFoldChange.keys.toArray
      val sigLength : Int = querySignature.size

      val randomSignatures : Set[QuerySignatureMap]  = (List.range(1, experiment.randomSignatureCount) map {
        i => randomSignatureGenerator.generateRandomSignature(geneIds, sigLength)
      }).toSet

      val maxConnectionsStrength : Float =
        connectivityMap.maximumConnectionStrengthUnordered(geneIds.size, querySignature.size)

      val results : Set[ConnectionScoreResult] = referenceSets map (refSet => {
        val avgFoldChangeProfile : ConnectivityMapReferenceProfile =
          referenceSetLoader.retrieveAverageReference(refSet)

        val c : ConnectivityMapConnectionScoreResult = connectivityMap.calculateConnectionScore(avgFoldChangeProfile, querySignature,
          randomSignatures, maxConnectionsStrength, refSet.filenames.size)

        ConnectionScoreResult(c.referenceSetName, c.connectionScore, c.pValue, c.setSize)
      })

      Some(ExperimentResult(experiment.id, results))
    }

    def runExperimentOrderedConnectionScore (experiment: Experiment) : Option[ExperimentResult] = {
      throw new UnsupportedOperationException("runExperimentOrderedConnectionScore not yet implemented")
    }

  }
}