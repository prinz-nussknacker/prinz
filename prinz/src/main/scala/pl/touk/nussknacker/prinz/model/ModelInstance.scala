package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}
import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import java.util.{Map => JMap}
import scala.concurrent.Future

abstract class ModelInstance(val model: Model, val signatureProvider: SignatureProvider) {

  private val signatureOption: ProvideSignatureResult =
    signatureProvider.provideSignature(model)

  def run(inputMap: ModelInputData): ModelRunResult

  def getSignature: ModelSignature = signatureOption match {
    case Some(value) => value
    case None => throw SignatureNotFoundException(this)
  }
}

object ModelInstance {

  type ModelRunResult = Future[Either[ModelRunException, JMap[String, _]]]

  type ModelInputData = VectorMultimap[String, AnyRef]
}
