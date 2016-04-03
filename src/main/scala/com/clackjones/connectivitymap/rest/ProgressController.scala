package com.clackjones.connectivitymap.rest

import org.json4s.{Formats, DefaultFormats}
import org.json4s.JsonAST.{JValue, JString, JArray}
import org.scalatra.scalate.ScalateSupport
import org.scalatra.{Ok, ScalatraServlet, NotFound}

import org.json4s.jackson.JsonMethods._

// JSON handling support from Scalatra
import org.scalatra.json._

case class Progress( percent : String )
case class SparkJobData(jobId : Int, numTasks : Int, numCompletedTasks : Int)

trait ProgressControllerComponent {
  val progressController = new ProgressController {}

  class ProgressController extends ScalatraServlet with ScalateSupport with JacksonJsonSupport {
    protected implicit lazy val jsonFormats: Formats = DefaultFormats


    get("/") {
      contentType = formats("json")

      getProgress() match {
        case Some(prog:String) => Ok(Progress(prog))
        case None => NotFound("Found no jobs")
      }
    }

    private def getProgress() : Option[String] = {

      applicationId() match {
        case Some(id : String) => {
          val jobStatus = latestJobProgress(id)
          Some((jobStatus.numCompletedTasks.toFloat / jobStatus.numTasks.toFloat * 100).toString())
        }
        case _ => None
      }


    }

    private def applicationId() : Option[String] = {
      val response = scala.io.Source.fromURL("http://localhost:4040/api/v1/applications").mkString

      val parsedApplication = parse(response, false)

      val appId = parsedApplication \\ "id"
      appId match {
        case s : JString => Some(s.extract[String])
        case _ => None
      }
    }

    private def latestJobProgress(applicationId : String) : SparkJobData = {
      val response = scala.io.Source.fromURL(f"http://localhost:4040/api/v1/applications/$applicationId/jobs").mkString

      val parsedResponse = parse(response, false)

      val latestJob : JValue = parsedResponse match {
        case a : JArray => a(0)
      }

      latestJob.extract[SparkJobData]
    }


  }
}

