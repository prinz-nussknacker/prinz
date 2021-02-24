package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import io.circe.Decoder
import pl.touk.nussknacker.prinz.mlflow.converter.MLFOutputDataTypeWrapper
import pl.touk.nussknacker.prinz.mlflow.model.api.{MLFModelLocationStrategy, MLFRegisteredModel}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.Dataframe
import pl.touk.nussknacker.prinz.model.ModelSignature
import pl.touk.nussknacker.prinz.util.http.AbstractRestJsonClient.RestClientResponse
import pl.touk.nussknacker.prinz.util.http.AsyncRestJsonClient

import scala.concurrent.Future

case class MLFInvokeRestClient(baseUrl: String, model: MLFRegisteredModel) {

  private val restClient = new AsyncRestJsonClient(baseUrl)

  def invoke(data: Dataframe, signature: ModelSignature, strategy: MLFModelLocationStrategy): Future[RestClientResponse[MLFOutputDataTypeWrapper]] = {
    implicit val decoder: Decoder[MLFOutputDataTypeWrapper] = MLFOutputDataTypeWrapper.getDecoderForSignature(signature)
    restClient.postJsonBody[Dataframe, MLFOutputDataTypeWrapper](data, strategy.createModelRelativeUrl(model))
  }
}
