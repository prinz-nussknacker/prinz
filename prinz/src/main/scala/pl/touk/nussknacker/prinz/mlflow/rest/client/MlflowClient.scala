package pl.touk.nussknacker.prinz.mlflow.rest.client

import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.rest.MlflowConstants.{BASE_API_PATH, BASE_PREVIEW_API_PATH}
import pl.touk.nussknacker.prinz.mlflow.rest.client.MlflowClient.MlflowClientResponse
import pl.touk.nussknacker.prinz.mlflow.rest.model.{CreateExperimentRequest, CreateExperimentResponse, ListExperimentsResponse, ListRegisteredModelsResponse}
import pl.touk.nussknacker.prinz.util.http.{RestJsonClient, RestClientException}

class MlflowClient(hostUrl: URL) {

  private val restClient = new RestJsonClient(s"$hostUrl$BASE_API_PATH")

  private val previewRestClient = new RestJsonClient(s"$hostUrl$BASE_PREVIEW_API_PATH")

  def listExperiments: MlflowClientResponse[ListExperimentsResponse] =
    restClient.getJson[ListExperimentsResponse]("/experiments/list")

  def createExperiment(experiment: CreateExperimentRequest): MlflowClientResponse[CreateExperimentResponse] =
    restClient.postJsonBody[CreateExperimentRequest, CreateExperimentResponse]("/experiments/create", experiment)

  def listRegisteredModels: MlflowClientResponse[ListRegisteredModelsResponse] =
    previewRestClient.getJson[ListRegisteredModelsResponse]("/registered-models/list")
}

object MlflowClient {
  def apply(hostUrl: URL): MlflowClient = new MlflowClient(hostUrl)

  type MlflowClientResponse[RESPONSE] = Either[RestClientException, RESPONSE]
}
