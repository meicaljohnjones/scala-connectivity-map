package com.clackjones

package object connectivitymap {
  type QuerySignatureMap = Map[String, Float]

  val config = Map(
    "reffileLocation" -> "file:///home/mike/connectivity_map_resources/FDA_tsv_subset",
    "querySignatureLocation" -> "file:///home/mike/connectivity_map_resources/queries",
    "querySignature" -> "Estrogen",
    "randomSignatureCount" -> "30000"
  )
}
