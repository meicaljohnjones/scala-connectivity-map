package com.clackjones.connectivitymap.spark

import org.apache.spark.{SparkConf, SparkContext}

trait SparkContextComponent {
  private val conf = new SparkConf().setAppName("Simple Application").setMaster("local[4]")
  val sc : SparkContext =  new SparkContext(conf)

}