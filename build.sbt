lazy val commonSettings = Seq(
    organization := "com.clackjones",
    version := "1.0",
    scalaVersion := "2.11.6"
)

lazy val root = (project in file(".")).
  settings(
    name := "scala-connectivity-map",
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4",
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
  )