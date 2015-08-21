package com.clackjones.connectivitymap.spark

import com.clackjones.connectivitymap._

object SparkCmapHelperFunctions {
  val refPathLength = config("reffileLocation").length + 1

  def calculateConnectionScore(querySignature: Map[String, Float],
                               referenceSignatureFoldChange: Map[String, Float],
                               maxConnectionStrength: Float)  = {

    val connectionStrength = querySignature.foldLeft(0f){
      case (strength, (geneId, reg)) => strength + referenceSignatureFoldChange(geneId) * reg
    }

    connectionStrength / maxConnectionStrength
  }

  def filenameToRefsetName(filename: String) : String = {
    filename.substring(refPathLength, filename.lastIndexOf("_"))
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
