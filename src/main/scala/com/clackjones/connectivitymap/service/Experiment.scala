package com.clackjones.connectivitymap.service

case class Experiment (
  var id : String,
  querySignatureId : String,
  randomSignatureCount : Int
)

case class ExperimentResult(
  experimentId : String,
  scores: Iterable[ConnectionScoreResult]
)

case class ConnectionScoreResult(referenceSetName: String, connectionScore: Float, pValue: Float, setSize: Integer) {
  override def toString(): String = {
    /* for outputting to tab file */
    List(referenceSetName, connectionScore, pValue).mkString("\t")
  }
}
