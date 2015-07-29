package com.clackjones.connectivitymap.service

import java.util.regex.Pattern

import com.clackjones.connectivitymap.querysignature.QuerySignatureFileLoaderComponent
import com.clackjones.connectivitymap._
import java.io.File
import com.clackjones.connectivitymap.spark.SparkContextComponent
import org.apache.spark.rdd.RDD

import org.slf4j.LoggerFactory


trait QuerySignatureProviderComponent {
  def querySignatureProvider : QuerySignatureProvider

  trait QuerySignatureProvider {
    def findAll() : Set[QuerySignature]
    def find (signatureId: String) : Option[QuerySignature]
  }
}

case class QuerySignature(val name: String, val geneUpDown : Map[String, Float]) {

  /**
   *
   * @return true if ordered, false if unordered
   */
  def isOrderedSignature : Boolean = {
    geneUpDown.values find (regulationMagnitude => regulationMagnitude.abs > 1) match {
      case Some(x) => true
      case None => false
    }
  }
}

trait FileBasedQuerySignatureProviderComponent extends QuerySignatureProviderComponent
    with QuerySignatureFileLoaderComponent {

  def querySignatureProvider = new FileBasedQuerySignatureProvider

  class FileBasedQuerySignatureProvider extends QuerySignatureProvider {
    val queries = config("querySignatureLocation")

    override def findAll() : Set[QuerySignature] = {
      val queriesDir = new File(queries)
      if (!queriesDir.exists() || !queriesDir.isDirectory()) {
        return Set()
      }

      val signatureIds  = queriesDir.list() map (filename => {
        filename.substring(0, filename.length - 4)
      })

      (signatureIds map(sigId => {
        find(sigId).get
      })).toSet
    }

    override def find (signatureId: String) : Option[QuerySignature] = {
      querySignatureLoader.loadQuerySignature(s"$queries/$signatureId.sig") match {
        case Some(sig) => return Some(QuerySignature(signatureId, sig))
        case None => return None
      }
    }
  }
}

trait SparkQuerySignatureProviderComponent extends QuerySignatureProviderComponent {
  this: SparkContextComponent =>

  def querySignatureProvider = new SparkQuerySignatureProvider

  class SparkQuerySignatureProvider extends QuerySignatureProvider {
    val logger = LoggerFactory.getLogger(getClass())

    val pathToQueries = config("querySignatureLocation")
    val queries : RDD[(String, String)] = sc.wholeTextFiles(pathToQueries+"/*.sig").map(pair => {
      val queryAbsolutePath = pair._1
      val startQuerySigName : Int = queryAbsolutePath.lastIndexOf("/") + 1
      val stopQuerySignName : Int = queryAbsolutePath.indexOf(".sig")
      val querySignatureName = queryAbsolutePath.substring(startQuerySigName, stopQuerySignName)

      (querySignatureName, pair._2)
    })

    override def findAll() : Set[QuerySignature] = {
      val all = queries.collect() map (pair => {
        val sigName = pair._1

        val fileLines = pair._2.split("\n").toIterator
        val filteredFileLines = fileLines.filter (line => !line.trim().startsWith("#"))
        filteredFileLines.next() // skip header line

        val geneUpDown: Map[String, Float] = (filteredFileLines map (line => {
              QuerySignatureLineSplitter.splitLine(line)
        })).toMap

        QuerySignature(sigName, geneUpDown)
      })

      all.toSet
    }

    override def find (signatureId: String) : Option[QuerySignature] = {
      findAll().find(sig => sig.name.equals(signatureId))
    }
  }

  /**
   * NOTE: create singleton for this method to ensure only the method
   * gets serialized by spark. If splitLine was a member of SparkQuerySignature then
   * the whole object would be serialized to use the method splitLine.
   */
  object QuerySignatureLineSplitter {
    val logger = LoggerFactory.getLogger(getClass())
    val whitespacePattern = Pattern.compile("\\s+")

    def splitLine(line: String): (String, Float) = {
      val splitLine = whitespacePattern.split(line.trim())

      val geneName = splitLine(0)
      val geneStrength = splitLine(1)

      try {
        geneStrength.toFloat
      } catch {
        case n: NumberFormatException => logger.error(s"Couldn't parse '$line' to (String, Float) tuple")
      }

      (geneName, geneStrength.toFloat)
    }
  }

}
