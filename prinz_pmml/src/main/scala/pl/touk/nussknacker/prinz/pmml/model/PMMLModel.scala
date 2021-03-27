package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.{Evaluator, LoadingModelEvaluatorBuilder, ModelEvaluator}
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}
import pl.touk.nussknacker.prinz.pmml.model.PMMLModel.buildModelEvaluatorFromStream
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelPayload

import java.io.{File, InputStream}

final class PMMLModel(payload: PMMLModelPayload) extends Model {
  val evaluatorBuilder = new LoadingModelEvaluatorBuilder().load(inputStream)
  val optionalModelName = Option(evaluatorBuilder.getModel().getModelName())
  val evaluator: Evaluator = evaluatorBuilder.build()

  override def getName: PMMLModelName = extractName

  override def getVersion: ModelVersion = PMMLModelVersion(payload.version)

  override def toModelInstance: ModelInstance = new PMMLModelInstance(evaluator, this)

  private def extractName: PMMLModelName = {
    //TODO: imo we should firstly check evaluator, if it has metadata about model name
    //TODO: then if not, use name from filename

    PMMLModelName(payload.name)
  }
  
  try {
    evaluator.verify()
  } catch {
    case ex: PMMLException => throw new ModelNotValidException(this, ex)
  }
}

case class PMMLModelName(name: String) extends ModelName(name)

case class PMMLModelVersion(version: String) extends ModelVersion

object PMMLModel {
  def apply(payload: PMMLModelPayload): PMMLModel = new PMMLModel(payload)

  private def buildModelEvaluatorFromStream(inputStream: InputStream): ModelEvaluator[_] = {
    val model = new LoadingModelEvaluatorBuilder().load(inputStream).build()
    inputStream.close()
    model
  }
}
