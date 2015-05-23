package com.clackjones.connectivitymap

import com.clackjones.connectivitymap.utils.FileParsingUtil.splitLine

/**
 * A trait to describe the loading of a query signature (represented by a Map[String, Int])
 * from a source
 */
trait QuerySignatureLoader {

  /**
   * Load an individual query signature
   * @param path the path to the file containing the query signature
   * @return a map containing values mapping a gene's probe ID (String) to whether it should be
   *         upregulated (1) or down-regulated (-1)
   */
  def loadQuerySignature(path: String): Map[String, Int]
}

import java.util.regex.Pattern

import scala.io.Source

object QuerySignatureFileLoader extends QuerySignatureLoader {


  /**
   * Loads an individual query signature from a file.
   * Note: All lines beginning with # are skipped as well as the first line
   * of the file that isn't a comment which should be the heading names
   *
   * @param path the path to the file containing the query signature
   * @return a map containing values mapping a gene's probe ID (String) to whether it should be
   *         upregulated (1) or down-regulated (-1)
   */
  override def loadQuerySignature(path: String): Map[String, Int] = {
    val srcFile = Source.fromFile(path).getLines() filter (line => line.charAt(0) != '#')

    srcFile.next() // skip titles line

    (srcFile map (line => splitLine(line))).toMap
  }
}