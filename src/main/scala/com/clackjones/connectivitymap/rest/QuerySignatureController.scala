package com.clackjones.connectivitymap.rest

import com.clackjones.connectivitymap.service.FileBasedQuerySignatureProviderComponent
import org.scalatra.ScalatraServlet
import org.scalatra.scalate.ScalateSupport

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

class QuerySignatureController extends ScalatraServlet with ScalateSupport with JacksonJsonSupport
  with FileBasedQuerySignatureProviderComponent {

  protected implicit lazy val jsonFormats: Formats = DefaultFormats

  /**
   * retrieve all query signatures
   */
  get("/") {
    contentType = formats("json")

    querySignatureProvider.findAll()
  }

  /**
   * retrieve a specific query signature by name
   */
  get("/:name") {
    contentType = formats("json")

    querySignatureProvider.find(params("name"))
  }
}

