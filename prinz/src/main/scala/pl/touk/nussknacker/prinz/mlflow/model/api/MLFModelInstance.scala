package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.prinz.mlflow.model.api.MLFSignatureInterpreter.downloadSignature
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestInvokeBody
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFInvokeRestClient
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException, ModelSignature}

import java.net.URL

case class MLFModelInstance(runUrl: URL, model: MLFRegisteredModel) extends ModelInstance {

  private val restClient = new MLFInvokeRestClient(runUrl.toString, model)

  private val strategy: MLFModelLocationStrategy = LocalMLFModelLocationStrategy

  private val signatureOption: Option[ModelSignature] = downloadSignature(model.getVersion.runId)

  override def run(columns: List[String], data: List[List[Double]]): Either[ModelRunException, List[Double]] =
    restClient.invoke(MLFRestInvokeBody(columns, data), strategy)
      .left.map(new ModelRunException(_))

  override def getSignature: ModelSignature = signatureOption match {
    case Some(value) => value
    case None => throw MLFSignatureNotFoundException(model)
  }
}
