package com.clackjones.connectivitymap.service

import com.clackjones.connectivitymap.querysignature.QuerySignatureFileLoaderComponent
import com.clackjones.connectivitymap._
import java.io.File


trait QuerySignatureProviderComponent {
  def querySignatureProvider : QuerySignatureProvider

  trait QuerySignatureProvider {
    def findAll() : Set[QuerySignature]
    def find (signatureId: String) : Option[QuerySignature]
  }
}

case class QuerySignature(val name: String, val geneUpDown : Map[String, Int])

trait FileBasedQuerySignatureProviderComponent extends QuerySignatureProviderComponent
    with QuerySignatureFileLoaderComponent {

  def querySignatureProvider = new FileBasedQuerySignatureProvider

  class FileBasedQuerySignatureProvider extends QuerySignatureProvider {
    val queries = new File(getClass().getResource(config("querySignatureLocation")).toURI()).getAbsolutePath()

    override def findAll() : Set[QuerySignature] = {
      val queriesDir = new File(queries)
      if (!queriesDir.exists() || !queriesDir.isDirectory()) {
        return Set()
      }

      val signatureIds  = queriesDir.list() map (filename => {
        filename.substring(0, filename.length - 4)
      })

      (signatureIds map(sigId => {
        find(sigId).get
      })).toSet
    }

    override def find (signatureId: String) : Option[QuerySignature] = {
      querySignatureLoader.loadQuerySignature(s"$queries/$signatureId.sig") match {
        case Some(sig) => return Some(QuerySignature(signatureId, sig))
        case None => return None
      }
    }
  }
}
