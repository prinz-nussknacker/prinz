package pl.touk.nussknacker.prinz.model

abstract class ModelInstance {

  def run[DATA_TYPE](columns: List[String], data: List[DATA_TYPE]): Either[ModelRunException, Float]

  def getSignature: ModelSignature
}
