package com.clackjones.connectivitymap

import java.util.regex.Pattern
import com.clackjones.connectivitymap.utils.FileParsingUtil.splitLine

trait ReferenceProfileLoader {

  /**
   * Loads an individual ReferenceProfile
   * @param path path to the individual Reference Profile
   * @return a ReferenceProfile object
   */
  def loadReferenceProfile(path: String): ReferenceProfile
}


import scala.io.Source

object ReferenceProfileFileLoader extends ReferenceProfileLoader {
  /**
   * Loads an individual ReferenceProfile
   * @param path path to the individual Reference Profile
   * @return a ReferenceProfile object
   */
  override def loadReferenceProfile(path: String): ReferenceProfile = {
    val srcFile = Source.fromFile(path).getLines()

    //skip titles line
    srcFile.next()

    val geneFoldChange = (srcFile map (line => splitLine(line))).toMap
    val name = path match {
      case s if s.contains("/") => path.substring(s.lastIndexOf("/") + 1)
      case _ => path
    }

    new ReferenceProfile(name, geneFoldChange)
  }
}