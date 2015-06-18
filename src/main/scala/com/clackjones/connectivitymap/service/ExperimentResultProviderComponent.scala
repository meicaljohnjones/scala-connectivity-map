package com.clackjones.connectivitymap.service

import scala.collection.mutable.Set

trait ExperimentResultProviderComponent {
  def experimentResultProvider : ExperimentResultProvider

  trait ExperimentResultProvider {
    def find(experimentId: Int) : Option[ExperimentResult]
    def findAll() : Set[ExperimentResult]
    def add(result: ExperimentResult) : Unit
  }
}

trait InMemoryExperimentResultProviderComponent extends ExperimentResultProviderComponent {
  val experimentResultProvider = new InMemoryExperimentResultProvider

  class InMemoryExperimentResultProvider extends ExperimentResultProvider {
    private val experimentResultSet : Set[ExperimentResult] = Set()

    override def find(experimentId: Int) : Option[ExperimentResult] = {
      experimentResultSet.find(_.experimentId == experimentId)
    }

    override def findAll() : Set[ExperimentResult] = {
      experimentResultSet
    }

    override def add(result: ExperimentResult) : Unit = {
      experimentResultSet.add(result)
    }
  }
}