package com.clackjones.connectivitymap

import com.clackjones.connectivitymap.cmap.ConnectivityMapModule
import com.clackjones.connectivitymap.querysignature.{DefaultRandomSignatureGeneratorComponent, QuerySignatureFileLoaderComponent}
import com.clackjones.connectivitymap.referenceprofile.{ReferenceProfileFileLoaderComponent, ReferenceSetFileLoaderComponent, ReferenceSetCreatorByDrugDoseAndCellLineComponent}
import com.clackjones.connectivitymap.service._
import com.clackjones.connectivitymap.spark.SparkContextComponent

/**
 * Run an example connectivity map
 */
class ConnectivityMapServiceRunner {
  this: QuerySignatureProviderComponent with ReferenceSetProviderComponent with ExperimentRunnerComponent
  with InMemoryExperimentProviderComponent with InMemoryExperimentResultProviderComponent =>

  def runExample(): Unit = {
    println("Creating experiment object")
    val experiment = Experiment(-1, "Estrogen", 100)
    val experimentWithId = experimentProvider.add(experiment)

    println("Running experiment...")
    experimentRunner.runExperiment(experimentWithId)

    val result : ExperimentResult = experimentResultProvider.find(experimentWithId.id).get
    result.scores foreach (println)
  }
}

object Main {

  def main(args: Array[String]): Unit = {
    val connectivityMapRunner = new ConnectivityMapServiceRunner
      with InMemoryExperimentProviderComponent
      with SparkExperimentRunnerComponent with DefaultRandomSignatureGeneratorComponent
      with ReferenceSetFileLoaderComponent with ReferenceProfileFileLoaderComponent
      with SparkQuerySignatureProviderComponent with InMemoryExperimentResultProviderComponent
      with SparkContextComponent
      with ConnectivityMapModule with FileBasedReferenceSetProviderComponent
      with ReferenceSetCreatorByDrugDoseAndCellLineComponent

    connectivityMapRunner.runExample()
  }
}
