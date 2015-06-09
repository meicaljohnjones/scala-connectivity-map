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
    def calculateConnectionStrength(referenceProfile: ReferenceProfile, querySignature: QuerySignature): Float = {

      val strengths = querySignature.par.map { case (geneId, reg) => {
        val foldChange = referenceProfile.geneFoldChange(geneId)
        foldChange * reg
      }}

      strengths.par.foldLeft(0f)(_ + _)
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
                                 connectionStrength: (ReferenceProfile, QuerySignature) => Float,
                                 maximumConnectionStrength: Float): Float = {

      val strength = connectionStrength(profile, querySignature)
      def connectionStrengthToScore(strength: Float, maxStrength: Float): Float = strength / maxStrength

      connectionStrengthToScore(strength, maximumConnectionStrength)
    }

    def calculateConnectionScore(profile: ReferenceProfile, querySignature: QuerySignature,
                                 randomQuerySignatures : Iterable[QuerySignature],
                                 maximumConnectionStrength: Float, setSize: Int): ConnectionScoreResult = {

      val trueScore = connectivityMap.calculateConnectionScoreImpl(profile, querySignature, connectivityMap.calculateConnectionStrength,
        maximumConnectionStrength)

      val randomScores = randomQuerySignatures.par.map { sig =>
        connectivityMap.calculateConnectionScoreImpl(profile, sig, connectivityMap.calculateConnectionStrength,
          maximumConnectionStrength)
      }

      val pVal = randomScores.foldLeft(0f)((count, randScore) => {
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
