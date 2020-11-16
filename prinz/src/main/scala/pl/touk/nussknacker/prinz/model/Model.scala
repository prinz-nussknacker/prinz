package pl.touk.nussknacker.prinz.model

case class ModelName(name: String) {
  override def toString: String = name
}

abstract class Model {
  def getName(): ModelName
}
