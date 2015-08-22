package com.clackjones.connectivitymap

import com.clackjones.connectivitymap.cmap.ConnectivityMapModule
import com.clackjones.connectivitymap.querysignature.DefaultRandomSignatureGeneratorComponent
import com.clackjones.connectivitymap.referenceprofile.{ReferenceProfileFileLoaderComponent, ReferenceSetFileLoaderComponent, ReferenceSetCreatorByDrugDoseAndCellLineComponent}
import com.clackjones.connectivitymap.service._
import com.clackjones.connectivitymap.spark.SparkContextComponent

import org.slf4j.LoggerFactory
/**
 * Run an example connectivity map
 */
class ConnectivityMapServiceRunner {
  this: QuerySignatureProviderComponent with ReferenceSetProviderComponent with ExperimentRunnerComponent
  with InMemoryExperimentProviderComponent with InMemoryExperimentResultProviderComponent with SparkContextComponent =>
  val logger = LoggerFactory.getLogger(getClass())

  def run(): Unit = {
    logger.info("Starting up experimentRunner")
    experimentRunner.start()
    val randomSignaureCount = config("randomSignatureCount").toInt

    println("Creating experiment object")

    runExperiment(experimentRunner, 1, "Estrogen", randomSignaureCount)
    runExperiment(experimentRunner, 2, "prostate_unordered", randomSignaureCount)
    runExperiment(experimentRunner, 3, "prostate_ordered", randomSignaureCount)

    // clean up resources
    logger.info("Experiment complete.")
    sc.stop()
  }

  def runExperiment(experimentRunner: ExperimentRunner, experimentId: Int, querySignatureId: String, randomSignatureCount: Int) = {

    val experiment = Experiment(experimentId, querySignatureId, randomSignatureCount)

    logger.info(s"Running $querySignatureId experiment...")
    val beforeEstrogen = System.currentTimeMillis()

    experimentRunner.runExperiment(experiment)

    val afterEstrogen = System.currentTimeMillis()

    val timeTakenEstrogen = (afterEstrogen - beforeEstrogen) / 1000f
    logger.info(f"Time taken on $querySignatureId sig: $timeTakenEstrogen%.2f s")
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
