package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.model.rest.api.{GetAllRegisteredModelsRequest,
  GetAllRegisteredModelsResponse, GetRegisteredModelRequest, GetRegisteredModelResponse,
  GetRunRequest, GetRunResponse, MLFRestModelName, MLFRestRegisteredModel, MLFRestRun, MLFRestRunId}
import pl.touk.nussknacker.prinz.util.http.RestJsonClient
import pl.touk.nussknacker.prinz.util.http.RestJsonClient.RestClientResponse
import pl.touk.nussknacker.prinz.util.http.RestRequestParams.extractRequestParams

case class MLFRestClient(config: MLFRestClientConfig) {

  private val restClient = new RestJsonClient(config.getBaseApiBaseUrl)

  private val previewRestClient = new RestJsonClient(config.getPreviewApliBaseUrl)

  def listModels(params: Option[GetAllRegisteredModelsRequest] = None): RestClientResponse[List[MLFRestRegisteredModel]] =
    previewRestClient.getJson[GetAllRegisteredModelsResponse]("/registered-models/list", extractRequestParams(params))
      .right.map(_.registered_models)

  def getModel(name: MLFRestModelName): RestClientResponse[MLFRestRegisteredModel] =
    previewRestClient.getJson[GetRegisteredModelResponse](
      "/registered-models/get", GetRegisteredModelRequest(name.name))
      .right.map(_.registered_model)

  def getRunInfo(id: MLFRestRunId): RestClientResponse[MLFRestRun] = {
    restClient.getJson[GetRunResponse]("/runs/get", GetRunRequest(id.id))
      .right.map(_.run)
  }
}

case class MLFRestClientConfig(private val hostUrl: URL,
                               private val baseApiPath: String,
                               private val basePreviewApiPath: String) {

  def getBaseApiBaseUrl: String = s"$hostUrl$baseApiPath"

  def getPreviewApliBaseUrl: String = s"$hostUrl$basePreviewApiPath"
}
