package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult

trait Model {

  protected val signatureOption: ProvideSignatureResult

  // defines input of scored external model
  final def getSignature: ModelSignature = signatureOption match {
    case Right(value) => value
    case Left(exception) => throw SignatureNotFoundException(exception)
  }

  // defines input of enricher model
  def getParameterDefinition: ModelSignature = getSignature

  def getName: ModelName

  def getVersion: ModelVersion

  def toModelInstance: ModelInstance

  override def toString: String = s"Model $getName version: $getVersion"
}

class ModelName(name: String) {

  def internal: String = name

  override def toString: String = name
}

trait ModelVersion
