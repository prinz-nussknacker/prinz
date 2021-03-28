package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.{Evaluator, LoadingModelEvaluatorBuilder, ModelEvaluator, PMMLException}
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelNotValidException, ModelVersion}
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelPayload

import java.io.{File, InputStream}

final class PMMLModel(payload: PMMLModelPayload) extends Model {

  val evaluatorBuilder: LoadingModelEvaluatorBuilder = new LoadingModelEvaluatorBuilder().load(payload.inputStream)

  val optionalModelName: Option[String] = Option(evaluatorBuilder.getModel.getModelName)

  val evaluator: Evaluator = evaluatorBuilder.build()

  try {
    evaluator.verify()
  } catch {
    case ex: PMMLException => throw ModelNotValidException(this, ex)
  } finally {
    payload.inputStream.close()
  }

  override def getName: PMMLModelName = extractName

  override def getVersion: ModelVersion = PMMLModelVersion(payload.version)

  override def toModelInstance: ModelInstance = new PMMLModelInstance(evaluator, this)

  private def extractName: PMMLModelName = PMMLModelName(optionalModelName.getOrElse(payload.name))
}

case class PMMLModelName(name: String) extends ModelName(name)

case class PMMLModelVersion(version: String) extends ModelVersion

object PMMLModel {

  def apply(payload: PMMLModelPayload): PMMLModel = new PMMLModel(payload)
}
