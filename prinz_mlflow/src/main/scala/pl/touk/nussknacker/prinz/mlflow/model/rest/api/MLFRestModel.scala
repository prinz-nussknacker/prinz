package pl.touk.nussknacker.prinz.mlflow.model.rest.api

import io.circe.generic.JsonCodec

@JsonCodec case class MLFRestRegisteredModelVersion(name: String,
                                                    version: String,
                                                    creation_timestamp: String,
                                                    last_updated_timestamp: String,
                                                    current_stage: String,
                                                    source: String,
                                                    run_id: String,
                                                    status: String)

@JsonCodec case class MLFRestRegisteredModel(name: String,
                                             creation_timestamp: String,
                                             last_updated_timestamp: String,
                                             latest_versions: List[MLFRestRegisteredModelVersion])

case class MLFRestModelName(name: String)

case class MLFRestRunId(id: String)

@JsonCodec case class MLFRestInvokeBody(columns: List[String], data: List[List[Double]])

@JsonCodec case class MLFRestRunInfo(run_id: String,
                                     run_uuid: String,
                                     experiment_id: String,
                                     user_id: String,
                                     status: String,
                                     start_time: Long,
                                     end_time: Long,
                                     artifact_uri: String,
                                     lifecycle_stage: String)

@JsonCodec case class MLFRestMetric(key: String,
                                    value: Double,
                                    timestamp: Long,
                                    step: Long)

@JsonCodec case class MLFRestParam(key: String, value: String)

@JsonCodec case class MLFRestRunTag(key: String, value: String)

@JsonCodec case class MLFRestRunData(metrics: List[MLFRestMetric],
                                     params: List[MLFRestParam],
                                     tags: List[MLFRestRunTag])

@JsonCodec case class MLFRestRun(info: MLFRestRunInfo,
                                 data: MLFRestRunData)
