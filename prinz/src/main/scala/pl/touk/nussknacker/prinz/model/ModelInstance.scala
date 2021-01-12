package pl.touk.nussknacker.prinz.model

import scala.concurrent.Future

abstract class ModelInstance(model: Model, private val signatureInterpreter: SignatureInterpreter) {

  type ModelRunResult = Future[Either[ModelRunException, List[Double]]]

  private val signatureOption: Option[ModelSignature] =
    signatureInterpreter.downloadSignature(model)

  def run(columns: List[String], data: List[List[Double]]): ModelRunResult

  def getSignature: ModelSignature = signatureOption match {
    case Some(value) => value
    case None => throw SignatureNotFoundException(this)
  }
}
