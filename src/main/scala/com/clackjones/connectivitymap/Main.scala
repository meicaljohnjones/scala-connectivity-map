package com.clackjones.connectivitymap

import com.clackjones.connectivitymap.cmap.ConnectivityMapModule
import com.clackjones.connectivitymap.querysignature.DefaultRandomSignatureGeneratorComponent
import com.clackjones.connectivitymap.referenceprofile.{ReferenceProfileFileLoaderComponent, ReferenceSetFileLoaderComponent, ReferenceSetCreatorByDrugDoseAndCellLineComponent}
import com.clackjones.connectivitymap.service._
import com.clackjones.connectivitymap.spark.SparkContextComponent

/**
 * Run an example connectivity map
 */
class ConnectivityMapServiceRunner {
  this: QuerySignatureProviderComponent with ReferenceSetProviderComponent with ExperimentRunnerComponent
  with InMemoryExperimentProviderComponent with InMemoryExperimentResultProviderComponent with SparkContextComponent =>

  def runExample(): Unit = {
    println("Starting up experimentRunner")
    experimentRunner.start()

    println("Creating experiment object")
    val experimentId = 1
    val querySignature = config("querySignature")
    val randomSignaureCount = config("randomSignatureCount").toInt

    val experiment = Experiment(experimentId, querySignature, randomSignaureCount)
    val experimentWithId = experimentProvider.add(experiment)

    println("Running experiment...")
    experimentRunner.runExperiment(experimentWithId)

    val result : ExperimentResult = experimentResultProvider.find(experimentWithId.id).get
    result.scores foreach (println)

    // clean up resources
    sc.stop()
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
