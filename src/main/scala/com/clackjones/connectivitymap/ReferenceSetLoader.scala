package com.clackjones.connectivitymap

trait ReferenceSetLoader {
  def createReferenceSets(filenames: Iterable[String]): Iterable[ReferenceSet]
}

class ReferenceSetLoaderByExperimentId extends ReferenceSetLoader {
  override def createReferenceSets(filenames: Iterable[String]): Iterable[ReferenceSet] = {
    throw new UnsupportedOperationException("createReferenceSets not yet implemented")
  }
}