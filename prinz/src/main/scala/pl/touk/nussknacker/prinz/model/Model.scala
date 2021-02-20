package pl.touk.nussknacker.prinz.model

trait Model {

  def getName: ModelName

  def getVersion: ModelVersion

  def toModelInstance: ModelInstance
}

class ModelName(name: String) {

  def internal: String = name

  override def toString: String = name
}

trait ModelVersion
