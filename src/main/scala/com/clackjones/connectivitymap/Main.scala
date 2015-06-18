package com.clackjones.connectivitymap

import com.clackjones.connectivitymap.cmap.ConnectivityMapModule
import com.clackjones.connectivitymap.querysignature.{DefaultRandomSignatureGeneratorComponent, QuerySignatureFileLoaderComponent}
import com.clackjones.connectivitymap.referenceprofile.{ReferenceProfileFileLoaderComponent, ReferenceSetFileLoaderComponent, ReferenceSetCreatorByDrugDoseAndCellLineComponent}
import com.clackjones.connectivitymap.service._

/**
 * Run an example connectivity map
 */
class ConnectivityMapServiceRunner {
  this: QuerySignatureProviderComponent with ReferenceSetProviderComponent with ExperimentRunnerComponent
  with InMemoryExperimentProviderComponent with InMemoryExperimentResultProviderComponent =>

  def runExample(): Unit = {
    println("Creating experiment object")
    val experiment = Experiment(-1, "Estrogen", 30000)
    val experimentWithId = experimentProvider.add(experiment)

    println("Running experiment...")
    experimentRunner.runExperimentUnorderedConnectionScore(experimentWithId)

    val result : ExperimentResult = experimentResultProvider.find(experimentWithId.id).get
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
      with InMemoryExperimentProviderComponent with InMemoryExperimentResultProviderComponent

    connectivityMapRunner.runExample()
  }
}
