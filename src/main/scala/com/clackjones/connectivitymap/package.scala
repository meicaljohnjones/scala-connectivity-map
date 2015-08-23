package com.clackjones

package object connectivitymap {
  type QuerySignatureMap = Map[String, Float]

  val config = Map(
    "reffileLocation" -> "s3n://connectivity-map-spark/FDA_reffiles",
    "querySignatureLocation" -> "s3n://connectivity-map-spark/queries",
    "querySignature" -> "Estrogen",
    "randomSignatureCount" -> "30000",
    "outputPath" -> "s3://connectivity-map-spark/results"
  )
}
