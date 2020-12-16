package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import pl.touk.nussknacker.prinz.mlflow.model.api.{LocalMLFModelLocationStrategy, MLFModelLocationStrategy, MLFRegisteredModel}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.RestMLFInvokeBody
import pl.touk.nussknacker.prinz.util.http.RestJsonClient
import pl.touk.nussknacker.prinz.util.http.RestJsonClient.RestClientResponse

class MLFInvokeRestClient(baseUrl: String, model: MLFRegisteredModel) {

  private val restClient = new RestJsonClient(baseUrl)

  def invoke(body: RestMLFInvokeBody, strategy: MLFModelLocationStrategy): RestClientResponse[List[Double]] =
    restClient.postJsonBody[RestMLFInvokeBody, List[Double]](strategy.createModelRelativeUrl(model), body)
}
