package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.{Evaluator, LoadingModelEvaluatorBuilder, PMMLException}
import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelNotValidException, ModelSignatureLocationMetadata, ModelVersion}
import pl.touk.nussknacker.prinz.pmml.model.PMMLModel.extractName
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelPayload

final class PMMLModel(payload: PMMLModelPayload) extends Model {

  override protected val signatureOption: ProvideSignatureResult = {
    val metadata = PMMLModelSignatureLocationMetadata(payload)
    PMMLSignatureProvider.provideSignature(metadata)
  }

  override def toModelInstance: ModelInstance = {
    val evaluator = PMMLModelEvaluatorExtractor.extractModelEvaluator(payload)
    PMMLModelInstance(evaluator, this)
  }

  override protected val name: ModelName = {
    val optionalModelName = PMMLModelEvaluatorExtractor.extractModelName(payload)
    extractName(optionalModelName, payload)
  }

  override protected val version: ModelVersion = PMMLModelVersion(payload.version)
}

final case class PMMLModelName(name: String) extends ModelName(name)

final case class PMMLModelVersion(version: String) extends ModelVersion

final case class PMMLModelSignatureLocationMetadata(payload: PMMLModelPayload) extends ModelSignatureLocationMetadata

object PMMLModel {

  def apply(payload: PMMLModelPayload): PMMLModel = new PMMLModel(payload)

  private def extractName(optionalModelName: Option[String], payload: PMMLModelPayload): PMMLModelName =
    PMMLModelName(optionalModelName.getOrElse(payload.name))
}
