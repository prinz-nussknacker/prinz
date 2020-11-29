package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import pl.touk.nussknacker.prinz.mlflow.model.api.{LocalMLFModelLocation, MLFModelLocationStrategy, MLFRegisteredModel}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.RestMLFInvokeBody
import pl.touk.nussknacker.prinz.util.http.RestJsonClient
import pl.touk.nussknacker.prinz.util.http.RestJsonClient.RestClientResponse

class MLFInvokeRestClient(baseUrl: String, model: MLFRegisteredModel) {

  private val restClient = new RestJsonClient(baseUrl)

  private val strategy: MLFModelLocationStrategy = LocalMLFModelLocation

  def invoke(body: RestMLFInvokeBody): RestClientResponse[Double] =
    restClient.postJsonBody[RestMLFInvokeBody, List[Double]](strategy.createModelRelativeUrl(model), body)
      .right.map(_.head)
}
