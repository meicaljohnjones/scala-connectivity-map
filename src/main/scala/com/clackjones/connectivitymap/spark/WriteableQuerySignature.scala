package com.clackjones.connectivitymap.spark

import java.io.{DataInput, DataOutput}
import org.apache.hadoop.io.Writable

case class WritableQuerySignature(var foldChange: Iterable[(String, Float)]) extends Writable {

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