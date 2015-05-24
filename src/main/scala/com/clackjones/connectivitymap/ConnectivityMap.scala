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

  def connectionScore(profile: ReferenceProfile, querySignature: Map[String, Int],
                       connectionStrength: (ReferenceProfile, Map[String, Int]) => (String, Int),
                        maximumConnectionStrength: Int): (String, Float) = {

    val strength = connectionStrength(profile, querySignature)

    def connectionStrengthToScore(strengthTuple: (String, Int), maxStrength: Float): (String, Float) = {
      (strengthTuple._1, strengthTuple._2 / maxStrength)
    }

    connectionStrengthToScore(strength, maximumConnectionStrength)
  }

  /**
   * A method to generate a random Query gene signature using a list of gene IDs
   *
   * @param geneIds List of gene ID strings (e.g. from a <code>ReferenceProfile</code>
   * @param signatureLength The length of the signature to be generated
   * @param nextRandomGeneIndex A function that randomly generates integers in the range [0, profile.size)
   * @param nextRandomUpDown A function that randomly generates numbers 1 or -1
   */
  def generateRandomSignature(geneIds: List[String], signatureLength: Int,
                              nextRandomGeneIndex: () => Int, nextRandomUpDown: () => Int): Map[String, Int] = {

    val selectedGeneIds: List[String] =  List.range(0, signatureLength) map (i =>
      nextRandomGeneIndex()) map (id =>
      geneIds(id))

    val randomQuerySignature = (selectedGeneIds map {(_, nextRandomUpDown()) }).toMap

    randomQuerySignature
  }
}
