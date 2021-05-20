package pl.touk.nussknacker.prinz.h2o.model

import pl.touk.nussknacker.engine.api.typed.typing.Typed
import pl.touk.nussknacker.prinz.model.{Model, ModelSignature, ModelSignatureLocationMetadata, SignatureField, SignatureName, SignatureProvider, SignatureType}

object H2OSignatureProvider extends SignatureProvider {

  override def provideSignature(modelSignatureLocationMetadata: ModelSignatureLocationMetadata): Either[Exception, ModelSignature] =
    modelSignatureLocationMetadata match {
      case metadata: H2OModelSignatureLocationMetadata =>
        val genModel = metadata.genModel
        val inputNames = genModel.getNames.toList.dropRight(1)
        val outputName = genModel.getResponseName
        val signatureInputs = inputNames.map(name => fromNameDomainToSignatureField(name, genModel.getDomainValues(name)))
        val signatureOutputs = List(fromNameDomainToSignatureField(outputName, genModel.getDomainValues(outputName)))
        Right(ModelSignature(signatureInputs, signatureOutputs))
      case _ => Left(new IllegalArgumentException("H2OSignatureProvider can provide only H2OModels signatures"))
  }

  private def fromNameDomainToSignatureField(name: String, domain: Array[String]): SignatureField = {
    SignatureField(
      SignatureName(name),
      SignatureType(typingResult = if (domain == null) Typed[Double] else Typed[String])
    )
  }
}
