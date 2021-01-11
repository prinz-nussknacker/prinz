package pl.touk.nussknacker.prinz.model

trait SignatureInterpreter {

  def downloadSignature(model: Model): Option[ModelSignature]
}
