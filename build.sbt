lazy val commonSettings = Seq(
    organization := "com.clackjones",
    version := "1.0",
    scalaVersion := "2.11.6"
)

lazy val root = (project in file(".")).
  settings(
    name := "scala-connectivity-map"
  )


