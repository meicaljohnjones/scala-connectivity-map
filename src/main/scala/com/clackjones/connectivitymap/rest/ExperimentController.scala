package com.clackjones.connectivitymap.rest

import com.clackjones.connectivitymap.service.{ExperimentRunnerComponent, Experiment, ExperimentProviderComponent}
import org.scalatra.scalate.ScalateSupport
import org.scalatra.{NotFound, Ok, ScalatraServlet}

// JSON-related libraries
import org.json4s.{MappingException, DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

trait ExperimentControllerComponent {
  this: ExperimentProviderComponent with ExperimentRunnerComponent =>
  val experimentController = new ExperimentController

  class ExperimentController extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {
    protected implicit lazy val jsonFormats: Formats = DefaultFormats

    get("/") {
      contentType = formats("json")

      Ok(experimentProvider.findAll())
    }

    /**
     * retrieve a specific query signature by name
     */
    get("/id/:id") {
      contentType = formats("json")
      val id = params("id").toInt

      experimentProvider.find(id) match {
        case Some(sig) => Ok(sig)
        case None => NotFound(s"Could not find experiment with the name $id")
      }
    }

    /**
     * Add new experiment
     */
    post("/") {
      try {
        val experiment = parsedBody.extract[Experiment]
        val experimentWithId = experimentProvider.add(experiment)
        experimentRunner.runExperimentUnorderedConnectionScore(experimentWithId)
      } catch {
        case jsonMapping: MappingException => "Couldn't parse json object!"
      }

    }
  }
}

