package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult

trait SignatureProvider {

  def provideSignature(modelSignatureLocationMetadata: ModelSignatureLocationMetadata): ProvideSignatureResult
}

object SignatureProvider {

  type ProvideSignatureResult = Either[Exception, ModelSignature]

  def indexedOutputName(index: Int): SignatureName = SignatureName(s"output_$index")
}
