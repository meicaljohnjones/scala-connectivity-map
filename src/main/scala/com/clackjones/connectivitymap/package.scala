package com.clackjones

package object connectivitymap {
  type QuerySignatureMap = Map[String, Int]

  val config = Map(
    "reffileLocation" -> "/reffiles_subset",
    "querySignatureLocation" -> "/queries",
    "defaultSignatureCount" -> "30000"
  )
}
