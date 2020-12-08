// Dependency versions
val scalaV = "2.12.10"
val nussknackerV = "0.3.0"
val sttpV = "3.0.0-RC7"
val scalatestV = "3.2.2"
val minioS3V = "8.0.0"
val circeV = "0.13.0"
val testContainersV = "0.38.7"
val paradiseV = "2.1.1"
val typesafeConfigV = "1.4.1"
val typesafeLogV = "3.9.2"
val logbackV = "1.2.3"


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
    addCompilerPlugin("org.scalamacros" % "paradise" % paradiseV cross CrossVersion.full),
    Test / fork := true,
    libraryDependencies ++= {
      Seq(
        "com.softwaremill.sttp.client3" %% "core" % sttpV,
        "com.softwaremill.sttp.client3" %% "circe" % sttpV,
        "io.minio" % "minio" % minioS3V,
        "io.circe" %% "circe-core" % circeV,
        "io.circe" %% "circe-generic" % circeV,
        "io.circe" %% "circe-parser" % circeV,
        "io.circe" %% "circe-yaml" % circeV,
        "com.typesafe" % "config" % typesafeConfigV,
        "com.typesafe.scala-logging" %% "scala-logging" % typesafeLogV,
        "org.scala-lang" % "scala-reflect" % scalaV,
        "ch.qos.logback" % "logback-classic" % logbackV,
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
    libraryDependencies ++= {
      Seq(
        "pl.touk.nussknacker" %% "nussknacker-flink-api" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-flink-util" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-model-flink-util" % nussknackerV,
      )
    }
  )
  .dependsOn(prinz)
  .enablePlugins(PackPlugin)
