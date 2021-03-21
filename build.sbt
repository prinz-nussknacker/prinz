import sbtassembly.MergeStrategy

// Dependency versions
val scalaV = "2.12.10"
val nussknackerV = "0.3.0"
val sttpV = "3.0.0-RC7"
val scalatestV = "3.2.2"
val minioS3V = "8.0.0"
val circeV = "0.11.1"
val circeYamlV = "0.11.0-M1"
val jpmmlV = "1.5.11"
val jpmmlTranspilerV = "1.1.7"
val testContainersV = "0.38.8"
val paradiseV = "2.1.1"
val typesafeConfigV = "1.4.1"
val typesafeLogV = "3.9.2"
val logbackV = "1.2.3"
val h2V = "1.4.200"
val jsoupV = "1.13.1"

val h2oV = "3.32.0.5"

ThisBuild / organization := "pl.touk.nussknacker.prinz"
ThisBuild / version      := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion := scalaV
ThisBuild / envFileName  := ".env"

def prinzMergeStrategy: String => MergeStrategy = {
  case PathList(ps@_*) if ps.last == "NumberUtils.class" => MergeStrategy.first
  case PathList(ps@_*) if ps.last == "module-info.class" => MergeStrategy.first
  case PathList(ps@_*) if ps.last == "io.netty.versions.properties" => MergeStrategy.first
  case PathList("org", "w3c", "dom", "events", xs @ _*) => MergeStrategy.first
  case PathList("org", "apache", "commons", "logging", xs @ _*) => MergeStrategy.first
  case PathList("com", "sun", "activation", xs @ _*) => MergeStrategy.first
  case PathList("javax", "activation", xs @ _*) => MergeStrategy.first
  case PathList("org", "slf4j", "impl", xs @ _*) => MergeStrategy.first
  case x: Any => MergeStrategy.defaultMergeStrategy(x)
}

lazy val commonSettings = Seq(
  test in assembly := {},
  assemblyMergeStrategy in assembly := prinzMergeStrategy,
  scalaVersion := scalaV,
  addCompilerPlugin("org.scalamacros" % "paradise" % paradiseV cross CrossVersion.full),
  resolvers ++= Seq(
    "Sonatype snaphots" at "https://oss.sonatype.org/content/groups/public/",
  ),
  libraryDependencies ++= {
    Seq(
      "pl.touk.nussknacker" %% "nussknacker-process" % nussknackerV,
      "com.typesafe.scala-logging" %% "scala-logging" % typesafeLogV,
    )
  },
  scalastyleConfig := file("project/scalastyle_config.xml"),
  (scalastyleConfig in Test) := file("project/scalastyle_test_config.xml"),
  dependencyOverrides ++= Seq(
    "io.circe" %% "circe-core" % circeV,
    "io.circe" %% "circe-generic" % circeV,
    "io.circe" %% "circe-parser" % circeV
  )
)

lazy val root = (project in file("."))
  .aggregate(prinz, prinz_sample)
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    publish / skip := true
  )

lazy val prinz = (project in file("prinz"))
  .settings(commonSettings)
  .settings(
    name := "prinz",
    Test / fork := true,
    libraryDependencies ++= {
      Seq(
        "com.softwaremill.sttp.client3" %% "core" % sttpV,
        "com.softwaremill.sttp.client3" %% "circe" % sttpV,
        "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % sttpV,
        "io.minio" % "minio" % minioS3V,
        "io.circe" %% "circe-core" % circeV,
        "io.circe" %% "circe-generic" % circeV,
        "io.circe" %% "circe-parser" % circeV,
        "io.circe" %% "circe-yaml" % circeYamlV,
        "com.typesafe" % "config" % typesafeConfigV,
        "org.scala-lang" % "scala-reflect" % scalaV,
        "ch.qos.logback" % "logback-classic" % logbackV,
        "org.scalatest" %% "scalatest" % scalatestV % Test,
        "org.scalatest" %% "scalatest-funsuite" % scalatestV % Test,
        "com.h2database" % "h2" % h2V % Test,
        "com.dimafeng" %% "testcontainers-scala-scalatest" % testContainersV % Test,
      )
    }
  )

lazy val prinz_mlflow = (project in file("prinz_mlflow"))
  .settings(commonSettings)
  .settings(
    name := "prinz-mlflow",
    Test / fork := true
  )
  .dependsOn(prinz % "compile->compile;test->test")

lazy val prinz_pmml = (project in file("prinz_pmml"))
  .settings(commonSettings)
  .settings(
    name := "prinz-pmml",
    Test / fork := true,
    libraryDependencies ++= {
      Seq(
        "org.jpmml" % "pmml-evaluator" % jpmmlV,
        "org.jpmml" % "pmml-evaluator-extension" % jpmmlV,
        "org.jpmml" % "pmml-model" % jpmmlV,
        "org.jpmml" % "jpmml-transpiler" % jpmmlTranspilerV,
        "org.jsoup" % "jsoup" % jsoupV,
        "ch.qos.logback" % "logback-classic" % logbackV,
      )
    }
  )
  .dependsOn(prinz % "compile->compile;test->test")

lazy val prinz_h2o = (project in file("prinz_h2o"))
  .settings(commonSettings)
  .settings(
    name := "prinz-h2o",
    Test / fork := true,
    libraryDependencies ++= {
      Seq(
        "ai.h2o" % "h2o-genmodel" % h2oV,
        "ai.h2o" % "h2o-core" % h2oV,
      )
    }
  )
  .dependsOn(prinz % "compile->compile;test->test")

lazy val prinz_sample = (project in file("prinz_sample"))
  .settings(commonSettings)
  .settings(
    name := "prinz-sample",
    libraryDependencies ++= {
      Seq(
        "pl.touk.nussknacker" %% "nussknacker-process" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-model-flink-util" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-kafka-flink-util" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-ui" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-flink-manager" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-flink-api" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-flink-util" % nussknackerV,
      )
    }
  )
  .dependsOn(prinz)
  .dependsOn(prinz_mlflow)
  .dependsOn(prinz_pmml)
