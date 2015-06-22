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
    libraryDependencies += "org.webjars" % "bootstrap" % "3.3.5"
  )
