package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.{Evaluator, LoadingModelEvaluatorBuilder, ModelEvaluator}
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}
import pl.touk.nussknacker.prinz.pmml.model.PMMLModel.buildModelEvaluatorFromStream

import java.io.{File, InputStream}

final class PMMLModel(inputStream: InputStream) extends Model {
  //TODO we need evaluator in PMMLModel to get the signature
  //PMMLModelInstance should use this evaluator for scoring
  val evaluator: Evaluator = buildModelEvaluatorFromStream(inputStream)

  override def getName: ModelName = ???

  override def getVersion: ModelVersion = ???

  override def toModelInstance: ModelInstance = ???
}

object PMMLModel {

  def apply(inputStream: InputStream): PMMLModel = new PMMLModel(inputStream)

  private def buildModelEvaluatorFromStream(inputStream: InputStream): ModelEvaluator[_] = {
    val model = new LoadingModelEvaluatorBuilder().load(inputStream).build()
    inputStream.close()
    model
  }
}
