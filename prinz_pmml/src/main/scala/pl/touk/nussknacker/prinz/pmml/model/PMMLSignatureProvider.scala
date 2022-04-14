package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.{LoadingModelEvaluatorBuilder, ModelField}
import org.jpmml.model.PMMLException
import pl.touk.nussknacker.prinz.model.{
  ModelNotValidException, ModelSignature, ModelSignatureLocationMetadata,
  SignatureField, SignatureName, SignatureProvider, SignatureType
}
import pl.touk.nussknacker.prinz.pmml.converter.PMMLSignatureInterpreter.fromPMMLDataType
import pl.touk.nussknacker.prinz.util.resource.ResourceManager

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object PMMLSignatureProvider extends SignatureProvider {

  override def provideSignature(modelSignatureLocationMetadata: ModelSignatureLocationMetadata): Either[Exception, ModelSignature] =
    modelSignatureLocationMetadata match {
      case modelSignatureLocationMetadata: PMMLModelSignatureLocationMetadata =>
        ResourceManager.withOpened(modelSignatureLocationMetadata.payload.inputStreamSource()) { inputStream =>
          val evaluatorBuilder: LoadingModelEvaluatorBuilder = new LoadingModelEvaluatorBuilder().load(inputStream)
          val evaluator = evaluatorBuilder.build()
          try {
            evaluator.verify()
            val signatureInputs = evaluator.getInputFields.map(modelFieldToSignatureField).toList
            val signatureOutputs = evaluator.getTargetFields.map(modelFieldToSignatureField).toList
            Right(ModelSignature(signatureInputs, signatureOutputs))
          } catch {
            case ex: PMMLException => throw ModelNotValidException(ex)
          }
        }
      case _ => Left(new IllegalArgumentException("PMMLSignatureProvider can provide only PMMLModels signatures"))
  }

  private def modelFieldToSignatureField(modelField: ModelField): SignatureField =
    SignatureField(
      SignatureName(modelField.getName),
      SignatureType(fromPMMLDataType(modelField.getDataType.toString))
    )
}
