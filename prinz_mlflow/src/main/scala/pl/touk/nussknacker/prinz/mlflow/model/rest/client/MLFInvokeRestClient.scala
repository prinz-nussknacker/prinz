package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import io.circe.generic.decoding.ReprDecoder.deriveReprDecoder
import pl.touk.nussknacker.prinz.mlflow.model.api.{MLFModelLocationStrategy, MLFRegisteredModel}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestInvokeBody
import pl.touk.nussknacker.prinz.util.http.AbstractRestJsonClient.RestClientResponse
import pl.touk.nussknacker.prinz.util.http.AsyncRestJsonClient

import scala.concurrent.Future

case class MLFInvokeRestClient(baseUrl: String, model: MLFRegisteredModel) {

  private val restClient = new AsyncRestJsonClient(baseUrl)

  def invoke(body: MLFRestInvokeBody, strategy: MLFModelLocationStrategy): Future[RestClientResponse[List[Double]]] =
    restClient.postJsonBody[String, List[Double]](strategy.createModelRelativeUrl(model), body.body)
}
