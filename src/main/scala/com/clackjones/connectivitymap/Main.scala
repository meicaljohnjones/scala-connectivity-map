package com.clackjones.connectivitymap

import com.clackjones.connectivitymap.cmap.ConnectivityMapModule
import com.clackjones.connectivitymap.querysignature.{DefaultRandomSignatureGeneratorComponent, QuerySignatureFileLoaderComponent}
import com.clackjones.connectivitymap.referenceprofile.{ReferenceProfileFileLoaderComponent, ReferenceSetFileLoaderComponent, ReferenceSetCreatorByDrugDoseAndCellLineComponent}
import com.clackjones.connectivitymap.service._

/**
 * Run an example connectivity map
 */
class ConnectivityMapServiceRunner {
  this: QuerySignatureProviderComponent with ReferenceSetProviderComponent with ExperimentRunnerComponent =>

  def runExample(): Unit = {
    println("Creating experiment object")
    val experiment = new Experiment
    experiment.querySignature = Some(querySignatureProvider.find("Estrogen"))
    experiment.refsets = Some(referenceSetProvider.findAll().toSet)
    experiment.randomSignatureCount = 30000

    println("Running experiment...")
    val experimentResult = experimentRunner.runExperimentUnorderedConnectionScore(experiment)

    val result : ExperimentResult = experimentResult.get
    result.scores foreach (println)
  }
}

object Main {

  def main(args: Array[String]): Unit = {
    val connectivityMapRunner = new ConnectivityMapServiceRunner
      with FileBasedQuerySignatureProviderComponent with QuerySignatureFileLoaderComponent
      with FileBasedReferenceSetProviderComponent with ReferenceSetCreatorByDrugDoseAndCellLineComponent
      with DefaultExperimentRunnerComponent with ConnectivityMapModule
      with ReferenceSetFileLoaderComponent with ReferenceProfileFileLoaderComponent
      with DefaultRandomSignatureGeneratorComponent

    connectivityMapRunner.runExample()
  }
}
