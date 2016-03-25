package com.clackjones.connectivitymap.service

import com.clackjones.connectivitymap._
import com.clackjones.connectivitymap.spark.SparkContextComponent
import org.apache.spark.SparkContext

import scala.collection.mutable.Set

trait ExperimentResultProviderComponent {
  def experimentResultProvider : ExperimentResultProvider

  trait ExperimentResultProvider {
    def find(experimentId: String) : Option[ExperimentResult]
    def findAll() : Set[ExperimentResult]
    def add(result: ExperimentResult) : Unit
  }
}

trait SparkExperimentResultProviderComponent extends ExperimentResultProviderComponent {
  this: SparkContextComponent =>
  val experimentResultProvider = new SparkExperimentResultProvider

  class SparkExperimentResultProvider extends ExperimentResultProvider {
    def find(experimentId: String) : Option[ExperimentResult] = {
      val experimentFilename = experimentId+".txt"
      try {
        val results = sc.objectFile[ConnectionScoreResult](config("outputPath") + "/" + experimentFilename).collect()
        Some(ExperimentResult(experimentId, results))
      } catch {
        case e : java.lang.Exception => None
      }
    }

    def findAll() : Set[ExperimentResult] = Set()

    def add(result : ExperimentResult) = throw new UnsupportedOperationException("Can't add Experiment result")
  }
}