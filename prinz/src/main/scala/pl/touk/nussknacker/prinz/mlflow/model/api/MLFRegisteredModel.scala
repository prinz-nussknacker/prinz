package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}
import java.time.Instant

case class MLFRegisteredModel(name: MLFRegisteredModelName,
                              creationTimestamp: Instant,
                              lastUpdatedTimestamp: Instant,
                              latestVersions: List[MLFRegisteredModelVersion]) extends Model {

  override def getName: ModelName = name

  override def getVersion: ModelVersion = latestVersions.maxBy(_.lastUpdatedTimestamp)

  override def toModelInstance: ModelInstance = MLFModelInstance("http://localhost:1234", this)
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
