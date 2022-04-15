import sbtassembly.MergeStrategy

val prinzV = "1.2.0-preview-staging-2022-03-14"
val prinzOrg = "pl.touk.nussknacker.prinz"
val repositoryOwner = "prinz-nussknacker"
val repositoryName = "prinz"

// Dependency versions
val scalaV = "2.12.10"
val nussknackerV = "1.2.0-staging-2021-12-09-5983-f15ac11992e3a82d32f651ef4e05b5d9458f46aa-SNAPSHOT"
val sttpV = "3.0.0-RC7"
val scalatestV = "3.2.2"
val minioS3V = "8.0.0"
val circeV = "0.14.1"
val circeYamlV = "0.11.0-M1"
val jpmmlV = "1.6.3"
val jpmmlTranspilerV = "1.1.7"
val testContainersV = "0.38.8"
val paradiseV = "2.1.1"
val typesafeConfigV = "1.4.1"
val typesafeLogV = "3.9.2"
val logbackV = "1.2.3"
val h2V = "1.4.200"
val jsoupV = "1.13.1"
val h2oV = "3.32.0.5"
val jaxbV = "3.0.0"
val jakartaV = "3.0.0"


ThisBuild / scalaVersion  := scalaV
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / envFileName   := ".env"


def prinzMergeStrategy: String => MergeStrategy = {
  case PathList(ps@_*) if ps.last == "NumberUtils.class" => MergeStrategy.first
  case PathList(ps@_*) if ps.last == "module-info.class" => MergeStrategy.first
  case PathList(ps@_*) if ps.last == "io.netty.versions.properties" => MergeStrategy.first
  case PathList("org", "w3c", "dom", "events", _*) => MergeStrategy.first
  case PathList("org", "apache", "commons", "logging", _*) => MergeStrategy.first
  case PathList("com", "sun", "activation", _*) => MergeStrategy.first
  case PathList("javax", "activation", _*) => MergeStrategy.first
  case PathList("org", "slf4j", "impl", _*) => MergeStrategy.first
  case PathList("org", "apache", "log4j", _*) => MergeStrategy.first
  case x: Any => MergeStrategy.defaultMergeStrategy(x)
}

lazy val commonSettings = Seq(
  organization := prinzOrg,
  version      := prinzV,
  scalaVersion := scalaV,

  resolvers ++= Seq(
    "Sonatype snaphots" at "https://oss.sonatype.org/content/groups/public/",
  ),

  publishMavenStyle := true,
  githubOwner       := repositoryOwner,
  githubRepository  := repositoryName,
  githubTokenSource := TokenSource.Or(
    TokenSource.Environment("GITHUB_TOKEN"),
    TokenSource.GitConfig("github.token")
  ),

  assembly / assemblyMergeStrategy := prinzMergeStrategy,
  assembly / test                  := {},
  scalastyleConfig        := file("project/scalastyle_config.xml"),
  Test / scalastyleConfig := file("project/scalastyle_test_config.xml"),
  Test / fork             := true,

  libraryDependencies ++= {
    Seq(
      "ch.qos.logback" % "logback-classic" % logbackV,
      "com.typesafe.scala-logging" %% "scala-logging" % typesafeLogV,
      "pl.touk.nussknacker" %% "nussknacker-util" % nussknackerV,
      "pl.touk.nussknacker" %% "nussknacker-api" % nussknackerV,
    )
  },

  dependencyOverrides ++= Seq(
    "io.circe" %% "circe-core" % circeV,
    "io.circe" %% "circe-generic" % circeV,
    "io.circe" %% "circe-parser" % circeV
  ),

  addCompilerPlugin("org.scalamacros" % "paradise" % paradiseV cross CrossVersion.full),
)

lazy val root = (project in file("."))
  .aggregate(prinz, prinz_sample)
  .settings(commonSettings)
  .settings(
    publishArtifact := false,
    publish / skip  := true
  )

lazy val prinz_util = (project in file("prinz_util"))
  .settings(commonSettings)
  .settings(
    name := "prinz-util",
    libraryDependencies ++= {
      Seq(
        "org.scala-lang" % "scala-reflect" % scalaV,

        "com.softwaremill.sttp.client3" %% "core" % sttpV,
        "com.softwaremill.sttp.client3" %% "circe" % sttpV,
        "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % sttpV,

        "io.circe" %% "circe-core" % circeV,
        "io.circe" %% "circe-generic" % circeV,
        "io.circe" %% "circe-parser" % circeV,
      )
    }
  )

lazy val prinz = (project in file("prinz"))
  .settings(commonSettings)
  .settings(
    name := "prinz",
    libraryDependencies ++= {
      Seq(
        "com.typesafe" % "config" % typesafeConfigV,
        "org.jsoup" % "jsoup" % jsoupV,

        "org.scalatest" %% "scalatest" % scalatestV % Test,
        "org.scalatest" %% "scalatest-funsuite" % scalatestV % Test,
        "com.dimafeng" %% "testcontainers-scala-scalatest" % testContainersV % Test,
      )
    }
  )

lazy val prinz_proxy = (project in file("prinz_proxy"))
  .settings(commonSettings)
  .settings(
    name := "prinz-proxy",
    libraryDependencies ++= {
      Seq(
        "com.h2database" % "h2" % h2V % Test,
      )
    }
  )
  .dependsOn(prinz_util % "compile->compile;test->test")
  .dependsOn(prinz % "compile->compile;test->test")

lazy val prinz_mlflow = (project in file("prinz_mlflow"))
  .settings(commonSettings)
  .settings(
    name := "prinz-mlflow",
    libraryDependencies ++= {
      Seq(
        "io.minio" % "minio" % minioS3V,
        "io.circe" %% "circe-core" % circeV,
        "io.circe" %% "circe-generic" % circeV,
        "io.circe" %% "circe-parser" % circeV,
        "io.circe" %% "circe-yaml" % circeYamlV,
      )
    }
  )
  .dependsOn(prinz_util % "compile->compile;test->test")
  .dependsOn(prinz % "compile->compile;test->test")
  .dependsOn(prinz_proxy % "test->test")

lazy val prinz_pmml = (project in file("prinz_pmml"))
  .settings(commonSettings)
  .settings(
    name := "prinz-pmml",
    libraryDependencies ++= {
      Seq(
        "org.jpmml" % "pmml-evaluator" % jpmmlV,
        "org.jpmml" % "pmml-model" % jpmmlV,
        "org.jpmml" % "jpmml-transpiler" % jpmmlTranspilerV,
        "org.jsoup" % "jsoup" % jsoupV,
        "jakarta.xml.bind" % "jakarta.xml.bind-api" % jakartaV,
        "com.sun.xml.bind" % "jaxb-impl" % jaxbV,
      )
    }
  )
  .dependsOn(prinz % "compile->compile;test->test")
  .dependsOn(prinz_proxy % "test->test")

lazy val prinz_h2o = (project in file("prinz_h2o"))
  .settings(commonSettings)
  .settings(
    name := "prinz-h2o",
    libraryDependencies ++= {
      Seq(
        "ai.h2o" % "h2o-genmodel" % h2oV,
        "ai.h2o" % "h2o-core" % h2oV,
      )
    }
  )
  .dependsOn(prinz % "compile->compile;test->test")
  .dependsOn(prinz_proxy % "test->test")

lazy val prinz_sample = (project in file("prinz_sample"))
  .settings(commonSettings)
  .settings(
    name := "prinz-sample",
    libraryDependencies ++= {
      Seq(
        // declare dependencies to Prinz when building outside Prinz repository
        // "pl.touk.nussknacker.prinz" %% "prinz" % prinzV,
        // "pl.touk.nussknacker.prinz" %% "prinz-mlflow" % prinzV,
        // "pl.touk.nussknacker.prinz" %% "prinz-pmml" % prinzV,
        // "pl.touk.nussknacker.prinz" %% "prinz-h2o" % prinzV,

        "pl.touk.nussknacker" %% "nussknacker-flink-engine" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-flink-kafka-util" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-ui" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-flink-manager" % nussknackerV,
        "pl.touk.nussknacker" %% "nussknacker-flink-api" % nussknackerV,
      )
    },
    // add GitHub packages resolver dependency with GitHub token declared to download prinz
    githubTokenSource := TokenSource.Or(
      TokenSource.Environment("GITHUB_TOKEN"),
      TokenSource.GitConfig("github.token")
    ),
    resolvers += Resolver.githubPackages(repositoryOwner, repositoryName),

    publishArtifact := false,
    publish / skip  := true,
  )
  // but actually in local build use the latest version of packages compiled locally
  .dependsOn(prinz % "compile->compile;test->test")
  .dependsOn(prinz_mlflow % "compile->compile;test->test")
  .dependsOn(prinz_pmml % "compile->compile;test->test")
  .dependsOn(prinz_h2o % "compile->compile;test->test")
