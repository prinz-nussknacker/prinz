package pl.touk.nussknacker.prinz.mlflow.rest.model

case class ModelVersionTag(key: String, value: String)

case class ModelVersion(name: String, version: String, creation_timestamp: String, last_updated_timestamp: String,
                        current_stage: String, source: String,
                        run_id: String, status: String, tags: List[ModelVersionTag])

case class RegisteredModelTag(key: String, value: String)

case class RegisteredModel(name: String, creation_timestamp: String, last_updated_timestamp: String,
                           latest_versions: List[ModelVersion], tags: List[RegisteredModelTag])

case class ListRegisteredModelsResponse(registered_models: List[RegisteredModel])