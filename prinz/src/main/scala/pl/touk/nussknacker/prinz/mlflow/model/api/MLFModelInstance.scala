package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.prinz.mlflow.model.rest.api.RestMLFInvokeBody
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFInvokeRestClient
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException}

case class MLFModelInstance(runUrl: String, model: MLFRegisteredModel) extends ModelInstance {

  private val restClient = new MLFInvokeRestClient(runUrl, model)

  override def run(columns: List[String], data: List[List[Double]]): Either[ModelRunException, Double] =
    restClient.invoke(RestMLFInvokeBody(columns, data))
      .left.map(new ModelRunException(_))

  override def getSignature: List[(String, String)] = ("Not", "implemented")::("yet", ".")::Nil
}

