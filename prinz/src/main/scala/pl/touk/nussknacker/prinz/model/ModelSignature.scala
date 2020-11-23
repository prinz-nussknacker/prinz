package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.exceptions.Assertion.assertIllegal

class ModelSignature private(signatureInputs: List[(String, SignatureType)], signatureOutputs: List[SignatureType]) {

  private val signatureMap = signatureInputs.groupBy(_._1).mapValues(_.head._2)

  def getSignatureNames: Iterable[String] = signatureMap.keys

  def getValueType(valueName: String): Option[SignatureType] = signatureMap.get(valueName)

  def getOutputType: List[SignatureType] = signatureOutputs
}

case class SignatureType(typeName: String)

object ModelSignature {

  def apply(signatureInputs: List[(String, SignatureType)], signatureOutputs: List[SignatureType]): ModelSignature = {
    verifyInitData(signatureInputs)
    new ModelSignature(signatureInputs, signatureOutputs)
  }

  private def verifyInitData(init: List[(String, SignatureType)]): Unit = {
    val foundInvalid = init.groupBy(_._1).values.map(_.size).exists(_ > 1)
    assertIllegal(!foundInvalid, "Model signature must consist of different keys names")
  }
}
