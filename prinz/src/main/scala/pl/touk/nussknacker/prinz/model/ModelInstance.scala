package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap
import java.util.{Map => JMap}
import scala.concurrent.Future

abstract class ModelInstance(model: Model, protected val signatureProvider: SignatureProvider) {

  type ModelRunResult = Future[Either[ModelRunException, JMap[String, _]]]

  private val signatureOption: Option[ModelSignature] =
    signatureProvider.provideSignature(model)

  def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult

  def getSignature: ModelSignature = signatureOption match {
    case Some(value) => value
    case None => throw SignatureNotFoundException(this)
  }
}
