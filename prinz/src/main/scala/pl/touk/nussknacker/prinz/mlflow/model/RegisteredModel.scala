package pl.touk.nussknacker.prinz.mlflow.model

import java.net.URL

import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}

case class ModelVersionTag(key: String, value: String)

case class RegisteredModelTag(key: String, value: String)

case class RegisteredModelVersion(name: String, version: String, creation_timestamp: String, last_updated_timestamp: String,
                                  current_stage: String, source: String,
                                  run_id: String, status: String, tags: List[ModelVersionTag]) extends ModelVersion {
  override def getModelInstance(): MLFModelInstance = new MLFModelInstance(new URL("http://localhost:1234"))
}

case class RegisteredModel(name: String, creation_timestamp: String, last_updated_timestamp: String,
                           latest_versions: List[RegisteredModelVersion], tags: List[RegisteredModelTag]) extends Model {
  override def getName(): ModelName = ModelName(name)

  override def getLatestVersion(): RegisteredModelVersion = latest_versions.head

  override def getVersions(): List[RegisteredModelVersion] = latest_versions
}
