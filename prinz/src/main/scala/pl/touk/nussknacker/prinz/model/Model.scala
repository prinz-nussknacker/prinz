package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult

trait Model {

  def getMetadata: ModelMetadata = ModelMetadata(getName, getVersion, getSignature, getParameterDefinition)

  def toModelInstance: ModelInstance

  protected val signatureOption: ProvideSignatureResult

  override def toString: String = s"Model $getName version: $getVersion"

  // defines input of scored external model
  protected final def getSignature: ModelSignature = signatureOption match {
    case Right(value) => value
    case Left(exception) => throw SignatureNotFoundException(exception)
  }

  // defines input of enricher model
  protected def getParameterDefinition: ModelSignature = getSignature

  protected def getName: ModelName

  protected def getVersion: ModelVersion
}

class ModelName(name: String) {

  def internal: String = name

  override def toString: String = name
}

trait ModelVersion
