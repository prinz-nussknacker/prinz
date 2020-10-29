// Dependency versions
val scalaV = "2.12.10"

ThisBuild / organization := "pl.touk.nussknacker.prinz"
ThisBuild / version      := "0.0.1-SNAPSHOT"
ThisBuild / scalaVersion := scalaV

lazy val commonSettings = Seq(
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
  )
