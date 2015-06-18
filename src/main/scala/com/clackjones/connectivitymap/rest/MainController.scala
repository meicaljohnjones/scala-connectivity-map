package com.clackjones.connectivitymap.rest

import org.scalatra.ScalatraServlet
import org.scalatra.scalate.ScalateSupport
import org.json4s.{DefaultFormats, Formats}

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

