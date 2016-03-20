lazy val commonSettings = Seq(
    organization := "com.clackjones",
    version := "0.1.0",
    scalaVersion := "2.11.6"
)

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

lazy val root = (project in file(".")).
  settings(
    name := "scala-connectivity-map",
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4",
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
    libraryDependencies += "org.apache.commons" % "commons-math3" % "3.2",
    libraryDependencies += "org.scalatra" %% "scalatra" % "2.3.1",
    libraryDependencies += "org.scalatra" %% "scalatra-scalate" % "2.3.1",
    libraryDependencies += "javax.servlet" % "javax.servlet-api" % "3.0.1",
    libraryDependencies += "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106",
    libraryDependencies += "org.scalatra" %% "scalatra-json" % "2.4.0.RC1",
    libraryDependencies += "org.json4s"   %% "json4s-jackson" % "3.3.0.RC1",
    libraryDependencies += "org.webjars" % "webjars-servlet-2.x" % "1.1",
    libraryDependencies += "org.webjars" % "bootstrap" % "3.3.5",
    libraryDependencies += "org.webjars" % "jquery" % "2.1.4",
    /* logging */
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.1" % "runtime",

    libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.1" % "provided"
  )

assemblyMergeStrategy in assembly := {
  case PathList("org", "apache", "commons", xs @ _*) => MergeStrategy.first
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList("org", "slf4j", xs @ _*)         => MergeStrategy.first
  case PathList("com", "google", "common", xs @ _*)         => MergeStrategy.first

  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

assemblyJarName in assembly := "spark-connectivity-map.jar"

mainClass in (Compile, run) := Some("com.clackjones.connectivitymap.JettyLauncher")
mainClass in (Compile, packageBin) := Some("com.clackjones.connectivitymap.JettyLauncher")

/* include "provided" library dependencies when do `sbt run` */
run in Compile <<= Defaults.runTask(fullClasspath in Compile, mainClass in (Compile, run), runner in (Compile, run))