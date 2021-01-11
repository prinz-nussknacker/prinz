package pl.touk.nussknacker.prinz.model

abstract class ModelInstance(model: Model, private val signatureInterpreter: SignatureInterpreter) {

  private val signatureOption: Option[ModelSignature] =
    signatureInterpreter.downloadSignature(model)

  def run(columns: List[String], data: List[List[Double]]): Either[ModelRunException, List[Double]]

  def getSignature: ModelSignature = signatureOption match {
    case Some(value) => value
    case None => throw SignatureNotFoundException(this)
  }
}
