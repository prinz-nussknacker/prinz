package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult

trait Model {

  val signatureProvider: SignatureProvider

  private val signatureOption: ProvideSignatureResult =
    signatureProvider.provideSignature(this)

  def getSignature: ModelSignature = signatureOption match {
    case Some(value) => value
    case None => throw SignatureNotFoundException(this)
  }

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
