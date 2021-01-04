package pl.touk.nussknacker.prinz.model

trait SignatureInterpreter {

  def downloadSignature(modelId: String): Option[ModelSignature]
}
