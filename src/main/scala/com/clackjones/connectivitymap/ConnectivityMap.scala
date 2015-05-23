package com.clackjones.connectivitymap

object ConnectivityMap {

  /**
   * Calculate the connection strength for one
   * @param referenceProfile a ReferenceProfile object
   * @param querySignature
   * @return a tuple containing the name of the reference profile and its connection strength
   */
  def connectionStrength(referenceProfile: ReferenceProfile, querySignature: Map[String, Int]): (String, Int) = {
    val keys = querySignature.keys.toSet

    val filteredReferenceProfile = referenceProfile.geneFoldChange filterKeys (refKey => keys contains refKey)

    val strengths = filteredReferenceProfile map {
      case (key, value) => {
        querySignature get key match {
          case Some(rank) => value * rank
        }
      }
    }

    (referenceProfile.name, strengths.toList.sum)
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

  def connectionScores(profiles: Set[ReferenceProfile], querySignature: Map[String, Int],
                       connectionStrength: (ReferenceProfile, Map[String, Int]) => (String, Int),
                        maximumConnectionStrength: Int): Set[(String, Float)] = {

    val connectionStrengths = profiles map (connectionStrength(_, querySignature))

    def connectionStrengthToScore (strengthTuple: (String, Int), maxStrength: Float): (String, Float) = {
      (strengthTuple._1, strengthTuple._2 /  maxStrength)
    }

    connectionStrengths map (connectionStrengthToScore(_, maximumConnectionStrength))
  }
}
