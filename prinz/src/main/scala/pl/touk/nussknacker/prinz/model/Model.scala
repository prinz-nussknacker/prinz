package pl.touk.nussknacker.prinz.model

case class ModelName(name: String) {
  override def toString: String = name
}

abstract class ModelVersion {
  def getModelInstance(): ModelInstance
}

abstract class Model {
  def getName(): ModelName

  def getLatestVersion(): ModelVersion
  def getVersions(): List[ModelVersion]
}
