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

  def run(): Unit = {
    println("Starting up experimentRunner")
    experimentRunner.start()
    val randomSignaureCount = config("randomSignatureCount").toInt

    println("Creating experiment object")

    runExperiment(experimentRunner, 1, "Estrogen", randomSignaureCount)
    runExperiment(experimentRunner, 2, "prostate_unordered", randomSignaureCount)
    runExperiment(experimentRunner, 3, "prostate_ordered", randomSignaureCount)

    // clean up resources
    sc.stop()
  }

  def runExperiment(experimentRunner: ExperimentRunner, experimentId: Int, querySignatureId: String, randomSignatureCount: Int) = {

    val experiment = Experiment(experimentId, querySignatureId, randomSignatureCount)

    println(s"Running $querySignatureId experiment...")
    val beforeEstrogen = System.currentTimeMillis()

    val result : ExperimentResult = experimentRunner.runExperiment(experiment) match {
      case Some(expRes) => expRes
      case None => throw new Exception("No Experiment result for $querySignatureId generated.")
    }

    result.scores foreach (println)
    val afterEstrogen = System.currentTimeMillis()

    val timeTakenEstrogen = (afterEstrogen - beforeEstrogen) / 1000f
    println(f"Time taken on $querySignatureId sig: $timeTakenEstrogen%.2f s")
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

    connectivityMapRunner.run()
  }
}
