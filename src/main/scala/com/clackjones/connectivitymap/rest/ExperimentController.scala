package com.clackjones.connectivitymap.rest

import com.clackjones.connectivitymap.service.{ExperimentRunnerComponent, Experiment, ExperimentProviderComponent}
import org.scalatra.scalate.ScalateSupport
import org.scalatra.{NotFound, Ok, ScalatraServlet}

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

class ExperimentController extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {
  this: ExperimentProviderComponent with ExperimentRunnerComponent =>

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
    val experiment = parsedBody.extract[Experiment]
    val experimentWithId = experimentProvider.add(experiment)

    experimentRunner.runExperimentUnorderedConnectionScore(experimentWithId)

    experimentWithId
  }
}

