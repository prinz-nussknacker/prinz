// Dependency versions
val scalaV = "2.12.10"
val nussknackerV = "0.2.2"
val sttpV = "3.0.0-RC7"
val json4sV = "3.6.0"
val scalatestV = "3.2.2"
val testContainersV = "0.38.6"


ThisBuild / organization := "pl.touk.nussknacker.prinz"
ThisBuild / version      := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion := scalaV

lazy val commonSettings = Seq(
  resolvers ++= Seq(
    "Sonatype snaphots" at "https://oss.sonatype.org/content/groups/public/",
  ),
  libraryDependencies ++= {
    Seq(
      "pl.touk.nussknacker" %% "nussknacker-process" % nussknackerV
    )
  },
  scalastyleConfig := file("project/scalastyle_config.xml"),
  (scalastyleConfig in Test) := file("project/scalastyle_test_config.xml"),
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
        "com.softwaremill.sttp.client3" %% "json4s" % sttpV,
        "org.json4s" %% "json4s-native" % json4sV,
        "org.scalatest" %% "scalatest" % scalatestV % Test,
        "org.scalatest" %% "scalatest-funsuite" % scalatestV % Test,
        "com.dimafeng" %% "testcontainers-scala-scalatest" % testContainersV % Test,
      )
    }
  )


lazy val prinz_sample = (project in file("prinz_sample"))
  .settings(commonSettings)
  .settings(
    name := "prinz-sample",
  )
  .dependsOn(prinz)
