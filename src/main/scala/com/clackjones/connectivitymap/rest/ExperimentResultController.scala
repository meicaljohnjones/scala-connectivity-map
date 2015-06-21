package com.clackjones.connectivitymap.rest

import com.clackjones.connectivitymap.service.{ExperimentResultProviderComponent, Experiment, ExperimentProviderComponent, ExperimentRunnerComponent}
import org.scalatra.scalate.ScalateSupport
import org.scalatra.{NotFound, Ok, ScalatraServlet}

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

trait ExperimentResultControllerComponent {
  this: ExperimentResultProviderComponent =>
  val experimentResultController = new ExperimentResultController

  class ExperimentResultController extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {

    protected implicit lazy val jsonFormats: Formats = DefaultFormats

    get("/") {
      contentType = formats("json")

      experimentResultProvider.findAll()
    }

    /**
     * retrieve a specific query signature by name
     */
    get("/id/:id") {
      contentType = formats("json")
      val id = params("id").toInt
      experimentResultProvider.find(id) match {
        case Some(result) => Ok(result)
        case None => NotFound(s"Could not find the experiment result with the id $id")
      }
    }
  }
}
