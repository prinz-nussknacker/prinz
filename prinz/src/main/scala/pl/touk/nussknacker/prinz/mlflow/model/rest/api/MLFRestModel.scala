package pl.touk.nussknacker.prinz.mlflow.model.rest.api

import io.circe.generic.JsonCodec

@JsonCodec case class RestRegisteredModelVersion(name: String,
                                                 version: String,
                                                 creation_timestamp: String,
                                                 last_updated_timestamp: String,
                                                 current_stage: String,
                                                 source: String,
                                                 run_id: String,
                                                 status: String)

@JsonCodec case class RestRegisteredModel(name: String,
                                          creation_timestamp: String,
                                          last_updated_timestamp: String,
                                          latest_versions: List[RestRegisteredModelVersion])

case class RestMLFModelName(name: String)

@JsonCodec case class RestMLFInvokeBody(columns: List[String], data: List[List[Double]])