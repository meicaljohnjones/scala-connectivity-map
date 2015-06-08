package com.clackjones.connectivitymap

import com.clackjones.connectivitymap.referenceprofile.ReferenceProfile

object ConnectivityMap {

  /**
   * Calculate the connection strength for one
   * @param referenceProfile a ReferenceProfile object
   * @param querySignature
   * @return a tuple containing the name of the reference profile and its connection strength
   */
  def calculateConnectionStrength(referenceProfile: ReferenceProfile, querySignature: QuerySignature): (String, Float) = {

    val strengths = querySignature.par.map {case (geneId, reg) => {
      val foldChange = referenceProfile.geneFoldChange(geneId)
      foldChange * reg
    }}

    (referenceProfile.name,strengths.par.foldLeft(0f)(_+_))
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

  def calculateConnectionScore(profile: ReferenceProfile, querySignature: QuerySignature,
                       connectionStrength: (ReferenceProfile, QuerySignature) => (String, Float),
                        maximumConnectionStrength: Float): (String, Float) = {

    val strength = connectionStrength(profile, querySignature)

    def connectionStrengthToScore(strengthTuple: (String, Float), maxStrength: Float): (String, Float) = {
      (strengthTuple._1, strengthTuple._2 / maxStrength)
    }

    connectionStrengthToScore(strength, maximumConnectionStrength)
  }
}
