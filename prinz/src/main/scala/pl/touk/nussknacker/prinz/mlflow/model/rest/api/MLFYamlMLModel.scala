package pl.touk.nussknacker.prinz.mlflow.model.rest.api

import io.circe.generic.JsonCodec

@JsonCodec case class MLFJsonSignature(inputs: String, outputs: String)

@JsonCodec case class MLFJsonMLModel(signature: MLFJsonSignature)

@JsonCodec case class MLFYamlInputDefinition(name: String, `type`: String)

@JsonCodec case class MLFYamlOutputDefinition(`type`: String)

@JsonCodec case class MLFYamlModelDefinition(inputs: List[MLFYamlInputDefinition],
                                             output: List[MLFYamlOutputDefinition])