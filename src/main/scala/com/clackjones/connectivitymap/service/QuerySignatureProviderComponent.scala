package com.clackjones.connectivitymap.service

import com.clackjones.connectivitymap.querysignature.{QuerySignatureLoaderComponent, QuerySignatureFileLoaderComponent}
import com.clackjones.connectivitymap._
import java.io.File


trait QuerySignatureProviderComponent {
  def querySignatureProvider : QuerySignatureProvider

  trait QuerySignatureProvider {
    def findAll() : Set[QuerySignature]
    def find (signatureId: String) : QuerySignature

  }

  case class QuerySignature(val name: String, val geneUpDown : Map[String, Int])
}

trait FileBasedQuerySignatureProvider extends QuerySignatureProviderComponent {
  this: QuerySignatureLoaderComponent =>

  def querySignatureProvider = new FileBasedQuerySignatureProvider

  class FileBasedQuerySignatureProvider extends QuerySignatureProvider {
    val queries = new File(getClass().getResource(config("querySignatureLocation")).toURI()).getAbsolutePath()

    override def findAll() : Set[QuerySignature] = {
      throw new UnsupportedOperationException("findAll not yet implemented")
    }

    override def find (signatureId: String) : QuerySignature = {
      val sig = querySignatureLoader.loadQuerySignature(s"$queries/$signatureId.sig")
      QuerySignature(signatureId, sig)
    }
  }
}
