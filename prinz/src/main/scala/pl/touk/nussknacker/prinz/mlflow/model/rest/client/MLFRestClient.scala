package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.MlflowConstants.{BASE_API_PATH, BASE_PREVIEW_API_PATH}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.{GetAllRegisteredModelsRequest, GetAllRegisteredModelsResponse,
  GetRegisteredModelRequest, GetRegisteredModelResponse, RestMLFModelName, RestRegisteredModel}
import pl.touk.nussknacker.prinz.util.http.RestRequestParams.extractRequestParams
import pl.touk.nussknacker.prinz.util.http.RestJsonClient
import pl.touk.nussknacker.prinz.util.http.RestJsonClient.RestClientResponse

case class MLFRestClient(hostUrl: URL) {

  private val restClient = new RestJsonClient(s"$hostUrl$BASE_API_PATH")

  private val previewRestClient = new RestJsonClient(s"$hostUrl$BASE_PREVIEW_API_PATH")

  def listModels(params: Option[GetAllRegisteredModelsRequest] = None): RestClientResponse[List[RestRegisteredModel]] =
    previewRestClient.getJson[GetAllRegisteredModelsResponse]("/registered-models/list", extractRequestParams(params))
      .right.map(_.registered_models)

  def getModel(name: RestMLFModelName): RestClientResponse[RestRegisteredModel] =
    previewRestClient.getJson[GetRegisteredModelResponse](
      "/registered-models/get", GetRegisteredModelRequest(name.name))
      .right.map(_.registered_model)
}
