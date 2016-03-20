h1. Gene Expression Connectivity Map for Apache Spark

This is an implementation of Gene Expression Connectivity Map based on _sscMap_
which was devised and written by Dr Shu-Dong Zhang and Dr Timothy Gant
(see [http://bmcbioinformatics.biomedcentral.com/articles/10.1186/1471-2105-10-236|here] for
more information).

This following is an attempt to reimplement the software above using the Apache Spark
framework to exploit a much larger set of reference profiles from the
[lincs.hms.harvard.edu|LINCS database].

The software is exposed as a RESTful interface and also provides a simple (as yet incomplete) HTML
interface that uses the REST interface to interact with the software using AJAX calls.

To access the web interface to Gene Expression Connectivity Map, visit <http://localhost:6789>

h3. Installation
To run the software locally:

* clone this repository
* Edit the properties in `/src/main/scala/com/clackjones/package.scala` to point to
  the relevant reference profiles, query signatures and a results directory on your local machine
* cd to the root of the project and run
```
sbt -mem 8192 run
```

h4. Standalone cluster
_These instructions require updating_

How to run on a Spark standalone cluster:

* clone this repository
* `cd scala-connectivity-map`
* `sbt assembly`
* `cd target/scala-2.10/`
* `$SPARK_HOME/bin/spark-submit spark://master-url:7077 scala-connectivity-map.jar`