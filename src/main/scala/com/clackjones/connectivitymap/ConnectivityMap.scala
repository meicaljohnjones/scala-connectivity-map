package com.clackjones.connectivitymap

import com.clackjones.connectivitymap.referenceprofile.ReferenceProfile

trait ConnectivityMapModule {
  def connectivityMap : ConnectivityMap = new ConnectivityMap

  class ConnectivityMap {

    /**
     * Calculate the connection strength for one
     * @param referenceProfile a ReferenceProfile object
     * @param querySignature
     * @return a tuple containing the name of the reference profile and its connection strength
     */
    def calculateConnectionStrength(referenceProfile: ReferenceProfile, querySignature: QuerySignature): (String, Float) = {

      val strengths = querySignature.par.map { case (geneId, reg) => {
        val foldChange = referenceProfile.geneFoldChange(geneId)
        foldChange * reg
      }}

      (referenceProfile.name, strengths.par.foldLeft(0f)(_ + _))
    }

    def maximumConnectionStrengthOrdered(totalNumberGenes: Int, genesInQuery: Int): Int = {
      (1 to genesInQuery map (i =>
        (totalNumberGenes - i + 1) * (genesInQuery - i + 1)
        )).sum
    }

    def maximumConnectionStrengthUnordered(totalNumberGenes: Int, genesInQuery: Int): Int = {
      (1 to genesInQuery map (i =>
        (totalNumberGenes - i + 1)
        )).sum
    }

    def calculateConnectionScoreImpl(profile: ReferenceProfile, querySignature: QuerySignature,
                                 connectionStrength: (ReferenceProfile, QuerySignature) => (String, Float),
                                 maximumConnectionStrength: Float): (String, Float) = {

      val strength = connectionStrength(profile, querySignature)

      def connectionStrengthToScore(strengthTuple: (String, Float), maxStrength: Float): (String, Float) = {
        (strengthTuple._1, strengthTuple._2 / maxStrength)
      }

      connectionStrengthToScore(strength, maximumConnectionStrength)
    }

    def calculateConnectionScore(profile: ReferenceProfile, querySignature: QuerySignature,
                                 randomQuerySignatures : Iterable[QuerySignature],
                                 connectionStrength: (ReferenceProfile, QuerySignature) => (String, Float),
                                 maximumConnectionStrength: Float, setSize: Int): ConnectionScoreResult = {

      val trueScoreTuple = connectivityMap.calculateConnectionScoreImpl(profile, querySignature, connectivityMap.calculateConnectionStrength,
        maximumConnectionStrength)

      val randomScores = randomQuerySignatures.par.map { sig =>
        connectivityMap.calculateConnectionScoreImpl(profile, sig, connectivityMap.calculateConnectionStrength,
          maximumConnectionStrength)
      }

      val trueScore = trueScoreTuple._2
      val pVal = randomScores.foldLeft(0f)((count, randScoreTuple) => {
        val randScore = randScoreTuple._2
        if (randScore >= trueScore) count + 1 else count
      }) / randomQuerySignatures.size

      new ConnectionScoreResult(profile.name, trueScore, pVal, setSize)

    }
  }

  case class ConnectionScoreResult(referenceSetName : String, connectionScore: Float, pValue: Float, setSize : Integer) {
    override def toString(): String = {
      List("ConnectionResult:",referenceSetName,
        "Score: "+connectionScore.toString,
        "P-value: "+pValue.toString).mkString("\n")
    }
  }
}
