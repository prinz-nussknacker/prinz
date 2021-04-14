package pl.touk.nussknacker.prinz.h2o.model

import hex.genmodel.GenModel
import pl.touk.nussknacker.engine.api.typed.typing.Typed
import pl.touk.nussknacker.prinz.model.{Model, ModelSignature, SignatureField, SignatureName, SignatureProvider, SignatureType}

object H2OSignatureProvider extends SignatureProvider {

  override def provideSignature(model: Model): Option[ModelSignature] = model match {
    case model: H2OModel =>
      val m = model.modelWrapper.m
      val inputNames = m.getNames.toList.dropRight(1)
      val outputName = m.getResponseName
      val signatureInputs = inputNames.map(name => fromNameDomainToSignatureField(name, m.getDomainValues(name)))
      val signatureOutputs = List(fromNameDomainToSignatureField(outputName, m.getDomainValues(outputName)))
      Option(ModelSignature(signatureInputs, signatureOutputs))
    case _ => throw new IllegalArgumentException("H2OSignatureInterpreter can interpret only H2OModels")
  }

  private def fromNameDomainToSignatureField(name: String, domain: Array[String]): SignatureField = {
    SignatureField(
      SignatureName(name),
      SignatureType(typingResult = if (domain == null) Typed[Double] else Typed[String])
    )
  }
}
