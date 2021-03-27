package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult

trait SignatureProvider {

  def provideSignature(model: Model): ProvideSignatureResult
}

object SignatureProvider {

  type ProvideSignatureResult = Option[ModelSignature]

  def indexedOutputName(index: Int): SignatureName = SignatureName(s"output_$index")
}
