package pl.touk.nussknacker.prinz.mlflow.model.rest.api

import io.circe.generic.JsonCodec

@JsonCodec case class GetAllRegisteredModelsResponse(registered_models: List[RestRegisteredModel])

@JsonCodec case class GetRegisteredModelResponse(registered_model: RestRegisteredModel)
