package com.clackjones.connectivitymap.service

case class Experiment (
  var id : Int,
  querySignatureId : String,
  randomSignatureCount : Int
)

case class ExperimentResult(
  experimentId : Int,
  scores: Iterable[ConnectionScoreResult]
)

case class ConnectionScoreResult(referenceSetName: String, connectionScore: Float, pValue: Float, setSize: Integer) {
  override def toString(): String = {
    List("ConnectionResult:", referenceSetName,
      "Score: " + connectionScore.toString,
      "P-value: " + pValue.toString).mkString("\n")
  }
}
