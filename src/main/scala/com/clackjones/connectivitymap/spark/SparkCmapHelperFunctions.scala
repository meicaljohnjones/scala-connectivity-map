package com.clackjones.connectivitymap.spark

import scala.util.Try
import scala.util.Success
import scala.util.Failure

import scala.util.Properties

import scala.util.matching.Regex

object SparkCmapHelperFunctions {
  val fileSeparatorRegex = new Regex("\\"+System.getProperty("file.separator"))
  val filenameParameterSep = "__"
  val filenameParameterRegex = new Regex(filenameParameterSep)

  def calculateConnectionScore(querySignature: Map[String, Float],
                               referenceSignatureFoldChange: Map[String, Float],
                               maxConnectionStrength: Float)  = {

    val connectionStrength = querySignature.foldLeft(0f){
      case (strength, (geneId, reg)) => strength + referenceSignatureFoldChange(geneId) * reg
    }

    connectionStrength / maxConnectionStrength
  }

  def filenameToRefsetName(filename: String) : Try[String] = {
    val filenameWithoutPath = fileSeparatorRegex.split(filename).last
    val params : Array[String] = filenameParameterRegex.split(filenameWithoutPath)
    if (params.size == 7) {
      Success(params(0) + filenameParameterSep + params(4))
    } else {
      Failure(new Exception("Invalid Reference Set filename: "+filename))
    }
  }

  def fileToRefProfile(fileContents : String): Map[String, Float] = {
    val lines = fileContents.split("\n").drop(1)

    (lines map (line => {
      val splitLine = line.split("\t")
      (splitLine(0), splitLine(1).toFloat)
    })).toMap
  }

  def calculateAverageFoldChange(fc1: Map[String, Float], fc2: Map[String, Float]) = {
    fc1.map{ case (geneId, foldChange) => {
      (geneId, (foldChange + fc2.getOrElse(geneId, 0f)) / 2f)
    }}
  }

}
