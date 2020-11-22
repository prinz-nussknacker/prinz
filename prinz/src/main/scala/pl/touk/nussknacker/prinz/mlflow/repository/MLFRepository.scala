package pl.touk.nussknacker.prinz.mlflow.repository

import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.model.RegisteredModel
import pl.touk.nussknacker.prinz.mlflow.MlflowConstants.{BASE_API_PATH, BASE_PREVIEW_API_PATH}
import pl.touk.nussknacker.prinz.model.ModelName
import pl.touk.nussknacker.prinz.model.repository.{ModelRepository, ModelRepositoryException}
import pl.touk.nussknacker.prinz.util.http.{RequestParams, RestJsonClient}

class MLFRepository(hostUrl: URL) extends ModelRepository {

  private val restClient = RestJsonClient(s"$hostUrl$BASE_API_PATH")

  private val previewRestClient = RestJsonClient(s"$hostUrl$BASE_PREVIEW_API_PATH")

  override def listModels: RepositoryResponse[List[RegisteredModel]] =
    previewRestClient.getJson[GetAllRegisteredModelsResponse]("/registered-models/list")
      .right.map(_.registered_models)
      .left.map(new MLFRepositoryRestException(_))

  def listModels(maxResults: Int, page: Int): RepositoryResponse[List[RegisteredModel]] =
    previewRestClient.getJson[GetAllRegisteredModelsRequest, GetAllRegisteredModelsResponse](
      "/registered-models/list", GetAllRegisteredModelsRequest(maxResults, page))
      .right.map(_.registered_models)
      .left.map(new MLFRepositoryRestException(_))

  override def getModel(name: ModelName): RepositoryResponse[RegisteredModel] =
    previewRestClient.getJson[GetRegisteredModelRequest, GetRegisteredModelResponse](
      "/registered-models/get", GetRegisteredModelRequest(name))
      .right.map(_.registered_model)
      .left.map(new MLFRepositoryRestException(_))
}

object MLFRepository {

  def apply(hostUrl: URL): MLFRepository = new MLFRepository(hostUrl)
}

case class GetAllRegisteredModelsRequest(max_results: Int, page_token: Int) extends RequestParams[GetAllRegisteredModelsRequest]
case class GetAllRegisteredModelsResponse(registered_models: List[RegisteredModel])

case class GetRegisteredModelRequest(name: ModelName) extends RequestParams[GetRegisteredModelRequest]
case class GetRegisteredModelResponse(registered_model: RegisteredModel)
