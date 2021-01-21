package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.Future

abstract class ModelInstance(model: Model, protected val signatureInterpreter: SignatureProvider) {

  type ModelRunResult = Future[Either[ModelRunException, List[Double]]]

  private val signatureOption: Option[ModelSignature] =
    signatureInterpreter.provideSignature(model)

  def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult

  def getSignature: ModelSignature = signatureOption match {
    case Some(value) => value
    case None => throw SignatureNotFoundException(this)
  }
}
