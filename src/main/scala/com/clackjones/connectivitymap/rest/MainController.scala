package com.clackjones.connectivitymap.rest

import org.scalatra.{NotFound, Ok, ScalatraServlet}
import org.scalatra.scalate.ScalateSupport

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

class MainController extends ScalatraServlet with ScalateSupport {
  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  /**
   * retrieve main index
   */
  get("/") {
    contentType = "text/html"
    ssp("index")
  }

}

