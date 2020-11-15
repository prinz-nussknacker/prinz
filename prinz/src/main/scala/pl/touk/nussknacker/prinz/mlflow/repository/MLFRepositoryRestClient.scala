package pl.touk.nussknacker.prinz.mlflow.repository

import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.model.RegisteredModel
import pl.touk.nussknacker.prinz.mlflow.MlflowConstants.{BASE_API_PATH, BASE_PREVIEW_API_PATH}
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.util.http.{RequestParams, RestClientException, RestJsonClient}


class MLFRepositoryRestClient(hostUrl: URL) extends ModelRepository {
  type RestResponse[RESPONSE] = Either[RestClientException, RESPONSE]

  private val restClient = new RestJsonClient(s"$hostUrl$BASE_API_PATH")

  private val previewRestClient = new RestJsonClient(s"$hostUrl$BASE_PREVIEW_API_PATH")

  override def listModels(): RestResponse[List[RegisteredModel]] =
    previewRestClient.getJson[GetAllRegisteredModelsResponse]("/registered-models/list").right.map(_.registered_models)

  def listModels(maxResults: Int, page: Int): RestResponse[List[RegisteredModel]] =
    previewRestClient.getJson[GetAllRegisteredModelsRequest, GetAllRegisteredModelsResponse](
      "/registered-models/list", GetAllRegisteredModelsRequest(maxResults, page))
      .right.map(_.registered_models)

  override def getModel(name: String): RestResponse[RegisteredModel] =
    previewRestClient.getJson[GetRegisteredModelRequest, GetRegisteredModelResponse](
      "/registered-models/get", GetRegisteredModelRequest(name))
      .right.map(_.registered_model)
}

object MLFRepositoryRestClient {
  def apply(hostUrl: URL): MLFRepositoryRestClient = new MLFRepositoryRestClient(hostUrl)
}

case class GetAllRegisteredModelsRequest(max_results: Int, page_token: Int) extends RequestParams[GetAllRegisteredModelsRequest]
case class GetAllRegisteredModelsResponse(registered_models: List[RegisteredModel])

case class GetRegisteredModelRequest(name: String) extends RequestParams[GetRegisteredModelRequest]
case class GetRegisteredModelResponse(registered_model: RegisteredModel)
