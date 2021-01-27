package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.prinz.model.{Model, ModelName, ModelVersion}

import java.time.Instant
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository

case class MLFRegisteredModel(name: MLFRegisteredModelName,
                              creationTimestamp: Instant,
                              lastUpdatedTimestamp: Instant,
                              latestVersions: List[MLFRegisteredModelVersion],
                              private val repository: MLFModelRepository) extends Model {

  override def getName: MLFRegisteredModelName = name

  override def getVersion: MLFRegisteredModelVersion = latestVersions.maxBy(_.lastUpdatedTimestamp)

  override def toModelInstance: MLFModelInstance = MLFModelInstance(repository.config, this)
}

case class MLFRegisteredModelName(name: String) extends ModelName(name)

case class MLFRegisteredModelVersion(name: String,
                                     version: String,
                                     creationTimestamp: Instant,
                                     lastUpdatedTimestamp: Instant,
                                     currentStage: String,
                                     source: String,
                                     runId: String,
                                     status: String) extends ModelVersion
