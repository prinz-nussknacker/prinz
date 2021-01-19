package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap
import pl.touk.nussknacker.prinz.util.nussknacker.NKConverter

import scala.concurrent.Future

abstract class ModelInstance(model: Model, protected val signatureInterpreter: SignatureInterpreter) {

  type ModelRunResult = Future[Either[ModelRunException, List[AnyRef]]]

  private val signatureOption: Option[ModelSignature] =
    signatureInterpreter.downloadSignature(model)

  def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult

  def getSignature: ModelSignature = signatureOption match {
    case Some(value) => value
    case None => throw SignatureNotFoundException(this)
  }
}
