package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.engine.api.typed.typing.TypingResult
import pl.touk.nussknacker.prinz.util.exceptions.Assertions.assertIllegal

class ModelSignature private(signatureInputs: List[(SignatureName, SignatureType)], signatureOutputs: List[SignatureType]) {

  private val signatureMap = signatureInputs.groupBy(_._1).mapValues(_.head._2)

  def getSignatureNames: List[SignatureName] = signatureMap.keys.toList

  def getValueType(valueName: SignatureName): Option[SignatureType] = signatureMap.get(valueName)

  def getOutputType: List[SignatureType] = signatureOutputs

  def getInputDefinition: List[(SignatureName, SignatureType)] = signatureInputs
}

case class SignatureName(name: String)

case class SignatureType(typingResult: TypingResult)

object ModelSignature {

  def apply(signatureInputs: List[(SignatureName, SignatureType)], signatureOutputs: List[SignatureType]): ModelSignature = {
    verifyInitData(signatureInputs)
    new ModelSignature(signatureInputs, signatureOutputs)
  }

  private def verifyInitData(init: List[(SignatureName, SignatureType)]): Unit = {
    val foundInvalid = init.groupBy(_._1).values.map(_.size).exists(_ > 1)
    assertIllegal(!foundInvalid, "Model signature must consist of different keys names")
  }
}
