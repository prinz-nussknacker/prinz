package pl.touk.nussknacker.prinz.model

trait SignatureProvider {

  def provideSignature(model: Model): Option[ModelSignature]
}

object SignatureProvider {

  def indexedOutputName(index: Int): SignatureName = SignatureName(s"output_$index")
}
