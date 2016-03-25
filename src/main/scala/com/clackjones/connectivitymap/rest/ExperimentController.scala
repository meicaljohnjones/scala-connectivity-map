package com.clackjones.connectivitymap.rest

import java.util.UUID

import com.clackjones.connectivitymap.service.{ExperimentQueueComponent, Experiment}
import org.scalatra.scalate.ScalateSupport
import org.scalatra.{Accepted, ScalatraServlet}

// JSON-related libraries
import org.json4s.{MappingException, DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

trait ExperimentControllerComponent {
  this: ExperimentQueueComponent =>
  val experimentController = new ExperimentController

  class ExperimentController extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {
    protected implicit lazy val jsonFormats: Formats = DefaultFormats

    /**
     * Add new experiment
     */
    post("/") {
      try {
        val experiment = parsedBody.extract[Experiment]
        experiment.id = UUID.randomUUID().toString
        experimentQueue.put(experiment)
        Accepted(headers = Map("location" -> f"/result/id/${experiment.id}"))
      } catch {
        case jsonMapping: MappingException => "Couldn't parse json object!"
      }
    }
  }
}

