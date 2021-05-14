package pl.touk.nussknacker.prinz.model.proxy.tranformer

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelSignature, SignatureProvider}

trait SignatureTransformer {

  def changeSignature(modelSignature: ModelSignature): ModelSignature
}

class TransformedSignatureProvider(transformer: SignatureTransformer) extends SignatureProvider {

  override def provideSignature(model: Model): ProvideSignatureResult =
    model.signatureProvider
      .provideSignature(model)
      .map(transformer.changeSignature)
}
