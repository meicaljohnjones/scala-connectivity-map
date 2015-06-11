package com.clackjones.connectivitymap.service

import com.clackjones.connectivitymap._

class Experiment {
  var refsets : Option[ReferenceSet] = None
  var querySignature : Option[QuerySignature] = None
  var randomSignatureCount : Int = config("defaultSignatureCount").toInt
  var experimentResult : Option[ExperimentResult] = None
}

class ExperimentResult(scores: Iterable[ConnectionScoreResult])

case class ConnectionScoreResult(referenceSetName: String, connectionScore: Float, pValue: Float, setSize: Integer) {
  override def toString(): String = {
    List("ConnectionResult:", referenceSetName,
      "Score: " + connectionScore.toString,
      "P-value: " + pValue.toString).mkString("\n")
  }
}
