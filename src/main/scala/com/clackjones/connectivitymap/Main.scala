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
    val randomSignaureCount = config("randomSignatureCount").toInt

    val experiment = Experiment(id = 1, querySignatureId = "Estrogen", randomSignaureCount)
    val experimentWithId = experimentProvider.add(experiment)

    println("Running Estrogen experiment...")
    val beforeEstrogen = System.currentTimeMillis()
    experimentRunner.runExperiment(experimentWithId)

    val result : ExperimentResult = experimentResultProvider.find(experimentWithId.id).get
    result.scores foreach (println)
    val afterEstrogen = System.currentTimeMillis()

    val timeTakenEstrogen = (afterEstrogen - beforeEstrogen) / 1000f
    println(f"Time taken on Estrogen sig: $timeTakenEstrogen%.2f s")

    //replace below with prostate cancer sigs
    val experiment2 = Experiment(id = 2, querySignatureId = "Estrogen", randomSignaureCount)
    val experimentWithId2 = experimentProvider.add(experiment2)

    println("Running Estrogen experiment...")
    val beforeEstrogen2 = System.currentTimeMillis()
    experimentRunner.runExperiment(experimentWithId2)

    val result2 : ExperimentResult = experimentResultProvider.find(experimentWithId2.id).get
    result2.scores foreach (println)
    val afterEstrogen2 = System.currentTimeMillis()

    val timeTakenEstrogen2 = (afterEstrogen2 - beforeEstrogen2) / 1000f
    println(f"Time taken on Estrogen (x2 time) sig: $timeTakenEstrogen2%.2f s")

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
