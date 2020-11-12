package pl.touk.nussknacker.prinz.mlflow.model

import pl.touk.nussknacker.prinz.model.Model

case class ModelVersionTag(key: String, value: String)

case class RegisteredModelTag(key: String, value: String)

case class ModelVersion(name: String, version: String, creation_timestamp: String, last_updated_timestamp: String,
                        current_stage: String, source: String,
                        run_id: String, status: String, tags: List[ModelVersionTag])

case class RegisteredModel(name: String, creation_timestamp: String, last_updated_timestamp: String,
                           latest_versions: List[ModelVersion], tags: List[RegisteredModelTag]) extends Model {
  override def getName(): String = name
}
