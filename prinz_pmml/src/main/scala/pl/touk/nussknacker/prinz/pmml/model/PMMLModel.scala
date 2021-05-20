package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.{Evaluator, LoadingModelEvaluatorBuilder, PMMLException}
import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelNotValidException, ModelSignatureLocationMetadata, ModelVersion}
import pl.touk.nussknacker.prinz.pmml.model.PMMLModel.extractName
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelPayload

final class PMMLModel(payload: PMMLModelPayload) extends Model {

  override protected val signatureOption: ProvideSignatureResult = PMMLSignatureProvider
    .provideSignature(PMMLModelSignatureLocationMetadata(payload))

  // TODO open model evaluator twice - for signature parse and scoring. Use ResourceManager to properly manage opened stream
  private val inputStream = payload.inputStreamSource()

  private val evaluatorBuilder: LoadingModelEvaluatorBuilder = new LoadingModelEvaluatorBuilder().load(inputStream)

  private val optionalModelName: Option[String] = Option(evaluatorBuilder.getModel.getModelName)

  val evaluator: Evaluator = evaluatorBuilder.build()

  try {
    evaluator.verify()
  } catch {
    case ex: PMMLException => throw ModelNotValidException(ex)
  } finally {
    inputStream.close()
  }

  override def toModelInstance: ModelInstance = PMMLModelInstance(evaluator, this)

  override protected def getName: PMMLModelName = extractName(optionalModelName, payload)

  override protected def getVersion: ModelVersion = PMMLModelVersion(payload.version)
}

final case class PMMLModelName(name: String) extends ModelName(name)

final case class PMMLModelVersion(version: String) extends ModelVersion

final case class PMMLModelSignatureLocationMetadata(payload: PMMLModelPayload) extends ModelSignatureLocationMetadata

object PMMLModel {

  def apply(payload: PMMLModelPayload): PMMLModel = new PMMLModel(payload)

  private def extractName(optionalModelName: Option[String], payload: PMMLModelPayload): PMMLModelName =
    PMMLModelName(optionalModelName.getOrElse(payload.name))
}
