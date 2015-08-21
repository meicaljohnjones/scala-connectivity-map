package com.clackjones.connectivitymap.service

import java.io.{DataOutput, DataInput}

import com.clackjones.connectivitymap._
import com.clackjones.connectivitymap.querysignature.RandomSignatureGeneratorComponent
import com.clackjones.connectivitymap.cmap.ConnectivityMapModule
import com.clackjones.connectivitymap.referenceprofile.ReferenceSetLoaderComponent
import com.clackjones.connectivitymap.spark.SparkContextComponent
import com.clackjones.connectivitymap.spark.SparkCmapHelperFunctions
import org.apache.hadoop.io.Writable

import org.apache.spark.Partitioner
import org.apache.spark.HashPartitioner
import org.apache.spark.rdd.RDD
import org.slf4j.LoggerFactory
import scala.util.Random

/**
 * Implementation note:
 *
 * If the QuerySignature for this experiment is ordered then ensure that
 * the ordered connectivity strength is calculated, likewise, if the QuerySignature
 * for this experiment is unordered, the connectivity strength calculated should be unordered.
 */
trait ExperimentRunnerComponent {
  type ConnectivityMapReferenceSet = com.clackjones.connectivitymap.referenceprofile.ReferenceSet
  type ConnectivityMapReferenceProfile = com.clackjones.connectivitymap.referenceprofile.ReferenceProfile
  type ConnectivityMapConnectionScoreResult = com.clackjones.connectivitymap.cmap.ConnectionScoreResult

  def experimentRunner : ExperimentRunner

  trait ExperimentRunner {
    def runExperiment(experiment: Experiment) : Option[ExperimentResult]
  }
}

/**
 * This implementation also takes the Experiment and adds
 * it to the ExperimentResultProvider when the ExperimentResult is created.
 */
trait DefaultExperimentRunnerComponent extends ExperimentRunnerComponent {
  this: RandomSignatureGeneratorComponent with ConnectivityMapModule
    with ReferenceSetLoaderComponent with ReferenceSetProviderComponent
    with QuerySignatureProviderComponent with ExperimentResultProviderComponent =>
  val experimentRunner = new DefaultExperimentRunner

  class DefaultExperimentRunner extends ExperimentRunner {

    def runExperiment(experiment: Experiment) : Option[ExperimentResult] = {

      val refsets : Set[ReferenceSet] = referenceSetProvider.findAll().toSet

      val referenceSets : Set[ConnectivityMapReferenceSet] = refsets map {
        s => new ConnectivityMapReferenceSet(s.name, s.filenames)
      }

      val serviceQuerySignature : Option[QuerySignature] = querySignatureProvider.find(experiment.querySignatureId)
      if (!serviceQuerySignature.isDefined) {
        return None
      }

      val querySignature : QuerySignatureMap = serviceQuerySignature.get.geneUpDown

      val firstRefSet : ConnectivityMapReferenceSet = referenceSets.toIterator.next()
      val setProfiles : Set[ConnectivityMapReferenceProfile] = referenceSetLoader.retrieveAllProfiles(firstRefSet)
      val firstProfile : ConnectivityMapReferenceProfile = setProfiles.toIterator.next()

      val geneIds : Array[String] = firstProfile.geneFoldChange.keys.toArray
      val sigLength : Int = querySignature.size

      val randomSignatures : Set[QuerySignatureMap]  = (List.range(1, experiment.randomSignatureCount) map {
        i => randomSignatureGenerator.generateRandomSignature(geneIds, sigLength)
      }).toSet

      val maxConnectionsStrength : Float = if (serviceQuerySignature.get.isOrderedSignature) {
        connectivityMap.maximumConnectionStrengthOrdered(geneIds.size, querySignature.size)
      } else {
        connectivityMap.maximumConnectionStrengthUnordered(geneIds.size, querySignature.size)
      }

      val results : Set[ConnectionScoreResult] = referenceSets map (refSet => {
        val avgFoldChangeProfile : ConnectivityMapReferenceProfile =
          referenceSetLoader.retrieveAverageReference(refSet)

        val c : ConnectivityMapConnectionScoreResult = connectivityMap.calculateConnectionScore(avgFoldChangeProfile, querySignature,
          randomSignatures, maxConnectionsStrength, refSet.filenames.size)

        ConnectionScoreResult(c.referenceSetName, c.connectionScore, c.pValue, c.setSize)
      })

      val experimentResult = ExperimentResult(experiment.id, results)
      experimentResultProvider.add(experimentResult)

      Some(experimentResult)
    }
  }
}

/**
 * This implementation also takes the Experiment and adds
 * it to the ExperimentResultProvider when the ExperimentResult is created.
 */
trait SparkExperimentRunnerComponent extends ExperimentRunnerComponent {
  this: SparkContextComponent with RandomSignatureGeneratorComponent with ConnectivityMapModule
    with ReferenceSetLoaderComponent with ReferenceSetProviderComponent
    with QuerySignatureProviderComponent with ExperimentResultProviderComponent =>
  val experimentRunner = new SparkExperimentRunner

  class SparkExperimentRunner extends ExperimentRunner {
    val logger = LoggerFactory.getLogger(getClass())

    def runExperiment(experiment: Experiment) : Option[ExperimentResult] = {

      logger.info("Starting to runExperiment...")
      logger.info("got stuff")

      logger.info("Loading query signatures...")
      val serviceQuerySignature : Option[QuerySignature] = querySignatureProvider.find(experiment.querySignatureId)
      if (!serviceQuerySignature.isDefined) {
        logger.warn ("Couldn't find the query signature you wanted")
        return None
      }

      val refPath = config("reffileLocation")

      val referenceSetsFilesRDD = sc.wholeTextFiles(refPath + "/*.gz", 5)

      val referenceSetNamesRDD =
        referenceSetsFilesRDD.keys map (SparkCmapHelperFunctions.filenameToRefsetName(_))

      val referenceSetValuesRDD =
        referenceSetsFilesRDD.values map (SparkCmapHelperFunctions.fileToRefProfile(_))

      val referenceSetsRDD = referenceSetNamesRDD.zip(referenceSetValuesRDD)
        .reduceByKey(SparkCmapHelperFunctions.calculateAverageFoldChange(_, _))

      val geneIds = referenceSetsFilesRDD
        .zipWithIndex().filter(_._2 == 1L) //take only one
        .map (_._1) //remove index
        .map { case (filname: String, contents: String) => SparkCmapHelperFunctions.fileToRefProfile(contents) }
        .first()
        .map { case (geneId: String, foldChange: Float) => geneId }

      val querySignature : QuerySignatureMap = serviceQuerySignature.get.geneUpDown
      val sigLength : Int = querySignature.size

      logger.info("Calculating random signatures...")

      val geneIdsBroadcast = sc.broadcast(geneIds.toList)

      val randomQuerySignaturesRDD : RDD[(Int, WritableQuerySignature)] = {
        sc.parallelize(List.range(0, experiment.randomSignatureCount, 1)) mapPartitionsWithIndex { case (partition, indices) => {
          val r = new Random()
          val _geneIds = geneIdsBroadcast.value

          val randomQuerySigs = indices map (i => {
            val randomGeneIds = r.shuffle(_geneIds)

            val geneIds = randomGeneIds.zipWithIndex.filter{case (geneId, idx) => idx < sigLength }.map{ case (geneId, idx) => geneId}
            val foldChange = Array.fill(sigLength)(if (r.nextInt(2) == 0) -1f else 1f)

            (i, WritableQuerySignature(geneIds.zip(foldChange)))
          })

          randomQuerySigs
        }}
      }

      val randomQuerySignatures = sc.broadcast(randomQuerySignaturesRDD.collect())

      logger.info("Finished calculating random signatures...")

      val maxConnectionsStrength : Float = if (serviceQuerySignature.get.isOrderedSignature) {
        connectivityMap.maximumConnectionStrengthOrdered(geneIds.size, querySignature.size)
      } else {
        connectivityMap.maximumConnectionStrengthUnordered(geneIds.size, querySignature.size)
      }

      val querySigBroadcast = sc.broadcast(querySignature)

      logger.info("Calculating results...")
      val results = referenceSetsRDD map { case (refSetName, avgFoldChange) => {


        val querySig = querySigBroadcast.value
        val randomQuerySigs = randomQuerySignatures.value
        val connectionScore = SparkCmapHelperFunctions.calculateConnectionScore(querySig, avgFoldChange, maxConnectionsStrength)

        val randomScores = randomQuerySigs map{ case (i, querySignature) => {
          SparkCmapHelperFunctions.calculateConnectionScore(querySignature.foldChange.toMap, avgFoldChange, maxConnectionsStrength)
        }}

        val pVal = randomScores.foldLeft(0f)((count, randScore) => {
          if (randScore >= connectionScore) count + 1 else count
        }) / randomQuerySigs.size

        (refSetName, connectionScore, pVal)
    }}

      val collectedResults = results.collect()

      val result = collectedResults map {
        case (refsetName, connectionScore, pVal) => ConnectionScoreResult(refsetName,
	     connectionScore, pVal, 0)
      }

      val experimentResult = ExperimentResult(experiment.id, result)
      experimentResultProvider.add(experimentResult)

      Some(experimentResult)
    }
  }
}



case class WritableQuerySignature(var foldChange: List[(String, Float)]) extends Writable {

  override def write(out: DataOutput): Unit = {
    out.writeInt(foldChange.size)
    // save the number of elements
    foldChange.foreach{ case (geneId, foldChange) => {
      out.writeUTF(geneId)
      out.writeFloat(foldChange)
    }}
  }

  override def readFields(in: DataInput): Unit = {
    val dataSetSize = in.readInt()

    val readFoldChange : List[(String, Float)] = (0 to dataSetSize).toList map (i => {
      (in.readUTF(), in.readFloat())
    })

    this.foldChange = readFoldChange
  }
}


class ReferenceSetPartitioner(val partitionCount: Int) extends Partitioner {
  val hashPartitioner = new HashPartitioner(partitionCount)

  override def numPartitions: Int = partitionCount

  override def getPartition(key: Any): Int = {
    // partition by refset name
    // extract refset name from path
    val path : String = key.toString()
    path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("_"))

    hashPartitioner.getPartition(path)
  }
}
