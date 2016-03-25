package com.clackjones.connectivitymap.service

import java.util.concurrent.ArrayBlockingQueue

trait ExperimentQueueComponent {
  val experimentQueue = new ArrayBlockingQueue[Experiment](10)
}
