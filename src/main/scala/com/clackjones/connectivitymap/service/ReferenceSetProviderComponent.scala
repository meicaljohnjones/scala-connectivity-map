package com.clackjones.connectivitymap.service

import java.io.File

import com.clackjones.connectivitymap._
import com.clackjones.connectivitymap.referenceprofile.{ReferenceSetCreatorByDrugDoseAndCellLineComponent, ReferenceSetCreatorComponent}

trait ReferenceSetProviderComponent {
  def referenceSetProvider : ReferenceSetProvider

  trait ReferenceSetProvider {
    def findAll() : Iterable[ReferenceSet]
  }

}

case class ReferenceSet(val name: String, val filenames: Iterable[String])

trait FileBasedReferenceSetProviderComponent extends ReferenceSetProviderComponent {
  this: ReferenceSetCreatorComponent =>
  val referenceSetProvider = new FileBasedReferenceSetProvider

  class FileBasedReferenceSetProvider extends ReferenceSetProvider {
    val reffiles: File = new File(config("reffileLocation"))

    override def findAll(): Iterable[ReferenceSet] = {
      val pathToFiles = reffiles.getAbsolutePath()
      val filenames = reffiles.list()

      val refsets: Iterable[ReferenceSet] = referenceSetCreator.createReferenceSets(pathToFiles, filenames) map {
        refset => ReferenceSet(refset.name, refset.filenames)
      }

      refsets
    }
  }
}
