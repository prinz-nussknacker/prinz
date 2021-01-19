package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap
import pl.touk.nussknacker.prinz.util.nussknacker.NKConverter

import scala.concurrent.Future

abstract class ModelInstance(model: Model, private val signatureInterpreter: SignatureInterpreter) {

  type ModelRunResult = Future[Either[ModelRunException, List[AnyRef]]]

  private val signatureOption: Option[ModelSignature] =
    signatureInterpreter.downloadSignature(model)

  def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult

  def getSignature: ModelSignature = signatureOption match {
    case Some(value) => value
    case None => throw SignatureNotFoundException(this)
  }

  def validateInput(input: VectorMultimap[String, AnyRef]): Boolean = {
    val signature = getSignature
    var result = true

    input.takeWhile(_ => result).foreach(el => {
      val(k, v) = el
      val require = signature.getInputValueType(SignatureName(k))
      val given = NKConverter.toTypingResult(v.getClass.toString)

      if(require.isEmpty) {
        result = false
      }

      else if(!given.canBeSubclassOf(require.get.typingResult)) {
        result = false
      }
    })

    result
  }
}
