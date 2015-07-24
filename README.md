# Gene Expression Connectivity Map for Apache Spark

How to run on a Spark standalone cluster:

* clone this repository
* `cd scala-connectivity-map`
* `sbt assembly`
* `cd target/scala-2.10/`
* `$SPARK_HOME/bin/spark-submit spark://master-url:7077 scala-connectivity-map.jar`

To access the web interface to Gene Expression Connectivity Map, visit <http://localhost:6789>
