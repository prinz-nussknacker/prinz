package pl.touk.nussknacker.prinz.mlflow.repository

import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.model.RegisteredModel
import pl.touk.nussknacker.prinz.mlflow.MlflowConstants.{BASE_API_PATH, BASE_PREVIEW_API_PATH}
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.util.http.{RestClientException, RestJsonClient}


class MLFRepositoryRestClient(hostUrl: URL) extends ModelRepository {
  type RestResponse[RESPONSE] = Either[RestClientException, RESPONSE]

  private val restClient = new RestJsonClient(s"$hostUrl$BASE_API_PATH")

  private val previewRestClient = new RestJsonClient(s"$hostUrl$BASE_PREVIEW_API_PATH")

  override def listModels(): RestResponse[List[RegisteredModel]] =
    previewRestClient.getJson[GetAllRegisteredModelsResponse]("/registered-models/list").right.map(_.registered_models)

  def listModels(maxResults: Int, page: Int): RestResponse[List[RegisteredModel]] =
    previewRestClient.getJsonBody[GetAllRegisteredModelsResponse]("/registered-models/list",
      listModelsBody(maxResults, page)).right.map(_.registered_models)

  override def getModel(name: String): RestResponse[RegisteredModel] =
    previewRestClient.getJsonBody[GetRegisteredModelResponse]("/registered-models/get",
      getModelBody(name)).right.map(_.registered_model)

  private def getModelBody(name: String) = s"""{"name": "${name}"}"""

  private def listModelsBody(maxResults: Int, page: Int) = s"""{"max_results": ${maxResults}, "page_token": ${page}}"""
}

object MLFRepositoryRestClient {
  def apply(hostUrl: URL): MLFRepositoryRestClient = new MLFRepositoryRestClient(hostUrl)
}

//RESPONSES
private case class GetAllRegisteredModelsResponse(registered_models: List[RegisteredModel])

private case class GetRegisteredModelResponse(registered_model: RegisteredModel)