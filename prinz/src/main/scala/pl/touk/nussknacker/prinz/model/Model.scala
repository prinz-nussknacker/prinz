package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult

trait Model {

  final def getMetadata: ModelMetadata = ModelMetadata(name, version, signature)

  def toModelInstance: ModelInstance

  protected val signatureOption: ProvideSignatureResult

  protected val name: ModelName

  protected val version: ModelVersion

  override def toString: String = s"Model $name version: $version"

  private final val signature: ModelSignature = signatureOption match {
    case Right(value) => value
    case Left(exception) => throw SignatureNotFoundException(exception)
  }
}

class ModelName(name: String) {

  def internal: String = name

  override def toString: String = name
}

trait ModelVersion
