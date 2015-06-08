package com.clackjones.connectivitymap.querysignature

import com.clackjones.connectivitymap.QuerySignature
import org.apache.commons.math3.random.RandomDataGenerator
import scala.collection.JavaConverters._

trait RandomSignatureGeneratorComponent {
  def randomSignatureGenerator : RandomSignatureGenerator

  trait RandomSignatureGenerator {
    def generateRandomSignature(geneIds: Array[String], signatureLength: Int) : QuerySignature
  }
}


trait DefaultRandomSignatureGeneratorComponent extends RandomSignatureGeneratorComponent {
  val randomSignatureGenerator = new DefaultRandomSignatureGenerator
  val randomDataGenerator = new RandomDataGenerator()

  class DefaultRandomSignatureGenerator extends RandomSignatureGenerator {

    /**
     * A method to generate a random Query gene signature using a list of gene IDs
     *
     * @param geneIds List of gene ID strings (e.g. from a <code>ReferenceProfile</code>
     * @param signatureLength The length of the signature to be generated
     */
    override def generateRandomSignature(geneIds: Array[String], signatureLength: Int) : QuerySignature = {
      val geneIdsJ = asJavaCollectionConverter(geneIds.toList).asJavaCollection

      val selectedGeneIds = randomDataGenerator.nextSample(geneIdsJ, signatureLength) map (obj => {
        obj match {
          case id : String => id
          case _ => throw new ClassCastException
        }
      })

      (selectedGeneIds map {(_, nextRandomUpDown()) }).toMap
    }

    def nextRandomUpDown() : Int = if (randomDataGenerator.nextInt(0, 1) == 0) return -1 else return 1

  }

}