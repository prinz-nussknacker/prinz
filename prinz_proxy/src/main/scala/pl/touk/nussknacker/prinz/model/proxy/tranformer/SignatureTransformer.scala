package pl.touk.nussknacker.prinz.model.proxy.tranformer

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.proxy.api.ProxiedModelSignatureLocationMetadata
import pl.touk.nussknacker.prinz.model.{ModelSignature, ModelSignatureLocationMetadata, SignatureProvider}

trait SignatureTransformer {

  def changeSignature(modelSignature: ModelSignature): ModelSignature
}

class TransformedSignatureProvider(transformer: SignatureTransformer) extends SignatureProvider {

  override def provideSignature(modelSignatureLocationMetadata: ModelSignatureLocationMetadata): ProvideSignatureResult =
  modelSignatureLocationMetadata match {
    case metadata: ProxiedModelSignatureLocationMetadata =>
      val signature = metadata.proxiedModel.getMetadata.signature
      Right(transformer.changeSignature(signature))
    case _: Any => Left(new IllegalArgumentException("TransformedSignatureProvider can provide signature only for ProxiedModels"))
  }
}
