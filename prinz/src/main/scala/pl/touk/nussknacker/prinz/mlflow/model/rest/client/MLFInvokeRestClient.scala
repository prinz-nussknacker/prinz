package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import pl.touk.nussknacker.prinz.mlflow.model.api.{MLFModelLocationStrategy, MLFRegisteredModel}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestInvokeBody
import pl.touk.nussknacker.prinz.util.http.RestJsonClient
import pl.touk.nussknacker.prinz.util.http.RestJsonClient.RestClientResponse

case class MLFInvokeRestClient(baseUrl: String, model: MLFRegisteredModel) {

  private val restClient = new RestJsonClient(baseUrl)

  def invoke(body: MLFRestInvokeBody, strategy: MLFModelLocationStrategy): RestClientResponse[List[Double]] =
    restClient.postJsonBody[MLFRestInvokeBody, List[Double]](strategy.createModelRelativeUrl(model), body)
}
