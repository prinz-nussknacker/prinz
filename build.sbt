val scalaV = "2.12.10"
val sttpV = "3.0.0-RC7"
val scalaTestV = "3.2.2"
val logbackV = "1.2.3"
val scalaLoggingV = "3.9.2"
val mlflowV = "1.1.0"
val nussknackerV = "2020-09-18-09-05-staging-dd7e4cdd9c01a8b2ed7b8f4a6a363c2d95e62b52-SNAPSHOT"

ThisBuild / organization := "pl.touk.nussknacker.prinz"
ThisBuild / version      := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion := scalaV

lazy val commonSettings = Seq(
  organization := "pl.touk.nussknacker.prinz",
  homepage := Some(url("https://github.com/prinz-nussknacker/prinz/")),
  resolvers ++= Seq(
    "Sonatype snaphots" at "https://oss.sonatype.org/content/groups/public/"
  ),
  scalaVersion := scalaV,
  scalacOptions := Seq(
    "-unchecked",
    "-deprecation",
    "-encoding", "utf8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-target:jvm-1.8"
  ),
  scalastyleConfig := file("project/scalastyle_config.xml")
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    publish / skip := true
  )

lazy val prinz = (project in file("prinz"))
  .settings(commonSettings)
  .settings(
    name := "prinz",
    libraryDependencies ++= {
      Seq(
        "com.softwaremill.sttp.client3" %% "core" % sttpV,
        "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV,
        "ch.qos.logback" % "logback-classic" % logbackV,
        "org.mlflow" % "mlflow-client" % mlflowV,
        "org.mlflow" % "mlflow-scoring" % mlflowV,
        "pl.touk.nussknacker" %% "nussknacker-http-utils" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-process" % nussknackerV,
        "org.scalatest" %% "scalatest" % scalaTestV % Test,
      )
    }
  )
