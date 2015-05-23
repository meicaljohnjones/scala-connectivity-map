package com.clackjones.connectivitymap.utils

import java.util.regex.Pattern

/**
 * A class for various functions specific to parsing ReferenceProfiles and
 * Query signatures from a file.
 */
object FileParsingUtil {
  val whitespacePattern = Pattern.compile("\\s+")

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
