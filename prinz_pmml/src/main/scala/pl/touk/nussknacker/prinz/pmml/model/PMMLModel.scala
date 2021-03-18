package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.Evaluator
import org.jpmml.evaluator.LoadingModelEvaluatorBuilder
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}

import java.io.{File, InputStream}

case class PMMLModel(inputStream: InputStream) extends Model {
  //TODO we need evaluator in PMMLModel to get the signature
  //PMMLModelInstance should use this evaluator for scoring
  val evaluator: Evaluator = new LoadingModelEvaluatorBuilder().load(inputStream).build()

  override def getName: ModelName = ???

  override def getVersion: ModelVersion = ???

  override def toModelInstance: ModelInstance = ???
}