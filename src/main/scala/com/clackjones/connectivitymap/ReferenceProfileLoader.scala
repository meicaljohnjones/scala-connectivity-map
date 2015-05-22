package com.clackjones.connectivitymap

import java.util.regex.Pattern

trait ReferenceProfileLoader {

  /**
   * Loads an individual ReferenceProfile
   * @param path path to the individual Reference Profile
   * @return a ReferenceProfile object
   */
  def loadReferenceProfile(path: String): ReferenceProfile

  /**
   * Loads a set of ReferenceProfiles
   * @param path path to the set of Reference Profiles
   * @return
   */
  def loadReferenceProfiles(path: String): Set[ReferenceProfile]
}


import scala.io.Source

object ReferenceProfileFileLoader extends ReferenceProfileLoader {
  val whitespacePattern = Pattern.compile("\\s+")
  /**
   * Loads an individual ReferenceProfile
   * @param path path to the individual Reference Profile
   * @return a ReferenceProfile object
   */
  override def loadReferenceProfile(path: String): ReferenceProfile = {
    val srcFile = Source.fromFile(path).getLines()

    val geneFoldChange = (srcFile map (line => splitLine(line))).toMap
    val name = path match {
      case s if s.contains("/") => path.substring(s.lastIndexOf("/") + 1)
      case _ => path
    }

    new ReferenceProfile(name, geneFoldChange)
  }

  /**
   * Loads a set of ReferenceProfiles
   * @param path path to the set of Reference Profiles
   * @return
   */
  override def loadReferenceProfiles(path: String): Set[ReferenceProfile] = {
    // TODO
    throw new NotImplementedError("loadReferenceProfiles not yet implemented")
  }

  def splitLine(line: String): (String, Int) = {
    val splitLine = whitespacePattern.split(line.trim())

    val geneName = splitLine(0)
    val geneStrength = splitLine(1) match {
      case s if s.endsWith(".0") => s.substring(0, s.length - 2)
      case s => s
    }

    (geneName, geneStrength.toInt)
  }
}