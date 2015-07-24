#!/bin/bash

PROJECT_HOME=~/workspace/scala-connectivity-map;
SPARK_MASTER_URL="spark://kembrek-Inspiron-7720:7077";
BUILT_ARTEFACT_NAME="spark-connectivity-map.jar"
VERBOSE=true;
STDOUT=/dev/stdout

[ -z $SPARK_HOME ] && { echo "ERROR: \$SPARK_HOME variable not set. Please set it and try again"; exit 1; };
[ -d $SPARK_HOME ] || { echo "ERROR: Could not find Apache Spark at $SPARK_HOME. Please set \$SPARK_HOME correctly and try again"; exit 1; };


if [[ "$VERBOSE" = false ]]; then
    STDOUT=/dev/null;
fi

cd $PROJECT_HOME || { echo "ERROR: '$PROJECT_HOME' does not exist. Please change the \$PROJECT_HOME variable in build-and-deploy.sh"; exit 1; };

echo "Assembling your project with sbt assembly";
sbt assembly > $STDOUT || { echo "ERROR: Assembly of script failed...exiting"; exit 1; };

echo "Shutting down existing Spark instance";
$SPARK_HOME/sbin/stop-all.sh; > $STDOUT;

echo "Restarting Spark instance";
[ -e "/tmp/spark-events" ] || mkdir /tmp/spark-events;
$SPARK_HOME/sbin/start-all.sh > $STDOUT || { echo "Failed to restart Apache Spark. Exiting..."; exit 1; };

echo "Submitting JAR to spark";
$SPARK_HOME/bin/spark-submit --master ${SPARK_MASTER_URL} ./target/scala-2.10/${BUILT_ARTEFACT_NAME} > $STDOUT || { echo "ERROR: Failed to submit $BUILT_ARTEFACT_NAME to Spark. Run this script in verbose mode for debugging purposes."; exit 1; };