package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.{Evaluator, LoadingModelEvaluatorBuilder, PMMLException}
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelNameNotFoundException, ModelNotValidException, ModelRunException, ModelVersion}

import java.io.InputStream

case class PMMLModel(inputStream: InputStream) extends Model {
  val evaluatorBuilder = new LoadingModelEvaluatorBuilder().load(inputStream)
  val optionalModelName = Option(evaluatorBuilder.getModel().getModelName())
  val evaluator: Evaluator = evaluatorBuilder.build()

  if (optionalModelName.isEmpty) {
    throw new ModelNameNotFoundException()
  }

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

case class PMMLModelVersion() extends ModelVersion
