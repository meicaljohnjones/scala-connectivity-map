package com.clackjones.connectivitymap.referenceprofile

import java.util.regex.Pattern

import scala.io.Source

trait ReferenceProfileLoaderComponent {
  def referenceProfileLoader: ReferenceProfileLoader

  trait ReferenceProfileLoader {

    /**
     * Loads an individual ReferenceProfile
     * @param path path to the individual Reference Profile
     * @return a ReferenceProfile object
     */
    def loadReferenceProfile(path: String): ReferenceProfile
  }

}

trait ReferenceProfileFileLoaderComponent extends ReferenceProfileLoaderComponent {
  def referenceProfileLoader = new ReferenceProfileFileLoader
  val whitespacePattern = Pattern.compile("\\s+")

  class ReferenceProfileFileLoader extends ReferenceProfileLoader {
    /**
     * Loads an individual ReferenceProfile
     * @param path path to the individual Reference Profile
     * @return a ReferenceProfile object
     */
    override def loadReferenceProfile(path: String): ReferenceProfile = {
      val srcFile = Source.fromFile(path)
      val lines = srcFile.getLines()

      //skip titles line
      lines.next()

      val geneFoldChange = (lines map (line => splitLine(line))).toMap
      val name = path match {
        case s if s.contains("/") => path.substring(s.lastIndexOf("/") + 1)
        case _ => path
      }
      srcFile.close()

      new ReferenceProfile(name, geneFoldChange)
    }
  }

  def splitLine(line: String): (String, Float) = {
    val splitLine = whitespacePattern.split(line.trim())

    val geneName = splitLine(0)
    val geneStrength = splitLine(1)

    (geneName, geneStrength.toFloat)
  }
}