package com.clackjones.connectivitymap.service

import scala.collection.mutable.Set

trait ExperimentProviderComponent {
  def experimentProvider : ExperimentProvider

  trait ExperimentProvider {
    def find(experimentId: Int) : Option[Experiment]
    def findAll() : Set[Experiment]

    /**
     * Add an experiment object and set its ID number
     * @param experiment
     * @return ID of this experiment
     */
    def add(experiment: Experiment) : Int
  }
}

trait InMemoryExperimentProviderComponent extends ExperimentProviderComponent {
  val experimentProvider : ExperimentProvider = new InMemoryExperimentProvider

  class InMemoryExperimentProvider extends ExperimentProvider {
    private val experimentSet : Set[Experiment] = Set[Experiment]()

    override def find(experimentId: Int): Option[Experiment] = {
      experimentSet.find(_.id == experimentId)
    }

    override def findAll(): Set[Experiment] = {
      experimentSet
    }

    override def add(experiment: Experiment): Int = {
      experiment.id = createNextExperimentId()
      experimentProvider.add(experiment)

      experiment.id
    }

    private def createNextExperimentId() : Int = {
      experimentSet.size
    }
  }
}
