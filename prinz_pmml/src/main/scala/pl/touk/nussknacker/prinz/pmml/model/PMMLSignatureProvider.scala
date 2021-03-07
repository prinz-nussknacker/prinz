package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.ModelField
import pl.touk.nussknacker.prinz.model._
import pl.touk.nussknacker.prinz.pmml.converter.PMMLSignatureInterpreter.fromPMMLDataType

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object PMMLSignatureProvider extends SignatureProvider {

  override def provideSignature(model: Model): Option[ModelSignature] = model match {
    case model: PMMLModel =>
      val evaluator = model.evaluator
      val signatureInputs = evaluator.getInputFields.map(modelFieldToSignatureField).toList
      val signatureOutputs = evaluator.getOutputFields.map(modelFieldToSignatureField).toList
      Option(ModelSignature(signatureInputs, signatureOutputs))
    case _ => throw new IllegalArgumentException("PMMLSignatureInterpreter can interpret only PMMLModels")
  }

  private def modelFieldToSignatureField(modelField: ModelField): SignatureField =
    SignatureField(SignatureName(modelField.getName.toString), SignatureType(fromPMMLDataType(modelField.getDataType.toString)))
}
