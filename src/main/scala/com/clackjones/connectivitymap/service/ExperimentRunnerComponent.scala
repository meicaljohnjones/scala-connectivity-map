package com.clackjones.connectivitymap.service

import java.io.File

import com.clackjones.connectivitymap._
import com.clackjones.connectivitymap.querysignature.RandomSignatureGeneratorComponent
import com.clackjones.connectivitymap.cmap.ConnectivityMapModule
import com.clackjones.connectivitymap.referenceprofile.ReferenceSetLoaderComponent
import com.clackjones.connectivitymap.spark.SparkContextComponent

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


      val querySignature : QuerySignatureMap = serviceQuerySignature.get.geneUpDown

      val referenceSetsRDD = SparkReferenceSetRDDCreator.loadReferenceSetsRDD()

      val firstRefSet = referenceSetsRDD.first()
      val geneIds: Array[String] = firstRefSet._2.keys.toArray
      val sigLength : Int = querySignature.size

      logger.info("Calculating random signatures...")

      val geneIdsBroadcast = sc.broadcast(geneIds.toList)

      val randomGeneIds : RDD[List[String]] = {
        sc.parallelize(List.range(0, experiment.randomSignatureCount, 1)) mapPartitionsWithIndex { case (partition, indices) => {
          val r = new Random()
          val _geneIds = geneIdsBroadcast.value

          val randomGeneIds = indices map (i => {
            val randomGeneIds = r.shuffle(_geneIds)

            randomGeneIds.zipWithIndex.filter{case (geneId, idx) => idx < sigLength }.map{ case (geneId, idx) => geneId}
          })

          randomGeneIds
        }}
      }

      val collectRandomGeneIds = randomGeneIds.collect()

      logger.info("Finished calculating random signatures...")

      val maxConnectionsStrength : Float = if (serviceQuerySignature.get.isOrderedSignature) {
        connectivityMap.maximumConnectionStrengthOrdered(geneIds.size, querySignature.size)
      } else {
        connectivityMap.maximumConnectionStrengthUnordered(geneIds.size, querySignature.size)
      }

      val querySigBroadcast = sc.broadcast(querySignature)

      logger.info("Calculating results...")
      val results = referenceSetsRDD map (refSet => {
        val avgFoldChange = refSet._2

        // TODO - use foldLeft instead of sum
        val connectionStrength = (querySigBroadcast.value map { case (geneId, reg) => {
          val foldChange = avgFoldChange(geneId)
          foldChange * reg
        }}).sum

        val connectionScore = connectionStrength / maxConnectionsStrength

        (refSet._1, connectionScore)

        //TODO calculate random scores
        //TODO use broadcast variables for QuerySignature and random scores
      })


      // TODO add random score and set size
      val result = (results map {
        case (refsetName, connectionScore) => ConnectionScoreResult(refsetName, connectionScore, 0f, 0)
      }).collect()

      val experimentResult = ExperimentResult(experiment.id, result)
      experimentResultProvider.add(experimentResult)

      Some(experimentResult)
    }
  }

  object SparkReferenceSetRDDCreator {
    val logger = LoggerFactory.getLogger(getClass())
    private val refsetsPath = new File(getClass().getResource(config("reffileLocation")).toURI()).getAbsolutePath()
    val hashPartitioner = new HashPartitioner(4)

    /**
      * Creates an RDD with key-pair of ReferenceSet name as the key and
      * then a collection of tuples which are the ReferenceProfiles containing
      * the name of the ReferenceProfile as well as the mapping of gene probe IDs
     * to their fold change.
      */
    def loadReferenceSetsRDD() = {
      val refsetsFileRDD = sc.wholeTextFiles("file://" + refsetsPath + "/*", minPartitions = 4).groupBy {
        case (filepath, contents) => filepath.substring(filepath.lastIndexOf("/") + 1, filepath.lastIndexOf("_"))
      }

      val refsetNameToProfileTuples = refsetsFileRDD map {
        case (refsetName, profiles) => {
          val geneFoldChanges = profiles flatMap { case (filename, fileContents) => {
            val lines = fileContents.split("\n").drop(1)

            (lines map (line => {
              val splitLine = line.split("\t")
              (splitLine(0), splitLine(1).toFloat)
            }))
          }}

          /** find the average gene fold change of all the profiles **/
          val avgGeneFoldChange = geneFoldChanges.groupBy(_._1) map (l => (l._1, (l._2 map (m => m._2) reduce (_ + _)) / 2f))

          (refsetName, avgGeneFoldChange)

        }
      }

      refsetNameToProfileTuples
    }

  }
}
