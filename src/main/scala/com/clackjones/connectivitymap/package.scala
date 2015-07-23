package com.clackjones

package object connectivitymap {
  type QuerySignatureMap = Map[String, Int]

  val config = Map(
    "reffileLocation" -> "/home/mike/connectivity_map_resources/reffiles_subset",
    "querySignatureLocation" -> "/home/mike/connectivity_map_resources/queries",
    "defaultSignatureCount" -> "30000"
  )
}
