package pl.touk.nussknacker.prinz.model

trait ModelInstance {

  def run(columns: List[String], data: List[List[Double]]): Either[ModelRunException, List[Double]]

  def getSignature: ModelSignature
}
