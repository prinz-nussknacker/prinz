package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.engine.api.definition.{NotBlankParameter, Parameter}
import pl.touk.nussknacker.engine.api.typed.typing.{TypedObjectTypingResult, TypingResult}
import pl.touk.nussknacker.prinz.util.exceptions.Assertions.assertIllegal

case class ModelSignature private(private val signatureInputs: List[SignatureField],
                                  private val signatureOutputs: List[SignatureField]) {

  private val signatureInputMap = signatureInputs.groupBy(_.signatureName).mapValues(_.head.signatureType)

  private val signatureOutputMap = signatureOutputs.groupBy(_.signatureName).mapValues(_.head.signatureType)

  def toInputParameterDefinition: List[Parameter] =
    signatureInputs.map(field => field.toNussknackerParameter)

  def toOutputTypedObjectTypingResult: TypedObjectTypingResult =
    TypedObjectTypingResult(signatureOutputMap.map(kv => (kv._1.name, kv._2.typingResult)).toList)

  def getSignatureInputs: List[SignatureField] = signatureInputs

  def getSignatureOutputs: List[SignatureField] = signatureOutputs

  def getInputValueType(valueName: SignatureName): Option[SignatureType] = signatureInputMap.get(valueName)

  def getOutputValueType(valueName: SignatureName): Option[SignatureType] = signatureOutputMap.get(valueName)

  override def toString: String = s"${getClass.getSimpleName}(\ninputs: $signatureInputs\noutputs: $signatureOutputs\n)"
}

case class SignatureName(name: String)

case class SignatureType(typingResult: TypingResult)

case class SignatureField(signatureName: SignatureName, signatureType: SignatureType) {

  def toNussknackerParameter: Parameter =
    NotBlankParameter(signatureName.name, signatureType.typingResult)

  override def toString: String =
    s"${signatureName.name}: ${signatureType.typingResult.display}"
}

object ModelSignature {

  def apply(signatureInputs: List[SignatureField], signatureOutputs: List[SignatureField]): ModelSignature = {
    verifyInitData(signatureInputs)
    new ModelSignature(signatureInputs, signatureOutputs)
  }

  private def verifyInitData(init: List[SignatureField]): Unit = {
    val foundInvalid = init.groupBy(_.signatureName).values.map(_.size).exists(_ > 1)
    assertIllegal(!foundInvalid, "Model signature must consist of different keys names")
  }
}
