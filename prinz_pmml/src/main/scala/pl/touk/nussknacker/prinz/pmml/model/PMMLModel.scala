package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.{Evaluator, LoadingModelEvaluatorBuilder, PMMLException}
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelNameNotFoundException, ModelNotValidException, ModelRunException, ModelVersion}

import java.io.InputStream

case class PMMLModel(inputStream: InputStream) extends Model {
  var evaluatorBuilder = new LoadingModelEvaluatorBuilder().load(inputStream)
  var optionalModelName = Option(evaluatorBuilder.getModel().getModelName())
  val evaluator: Evaluator = evaluatorBuilder.build()

  if (optionalModelName.isEmpty) {
    throw new ModelNameNotFoundException()
  }
  // TODO: Check (on repository level?) if model with such name exists

  try {
    evaluator.verify()
  } catch {
    case ex: PMMLException => throw new ModelNotValidException(this, ex)
  }

  override def getName: ModelName = PMMLModelName(optionalModelName.get)

  override def getVersion: ModelVersion = PMMLModelVersion()

  override def toModelInstance: ModelInstance = new PMMLModelInstance(evaluator, this)
}

case class PMMLModelName(name: String) extends ModelName(name)
