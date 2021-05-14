package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import java.util.{Map => JMap}
import scala.concurrent.Future

abstract class ModelInstance(val model: Model) {

  def run(inputMap: ModelInputData): ModelRunResult

  def getParameterDefinition: ModelSignature = model.getSignature

  override def toString: String = s"ModelInstance for: $model"
}

object ModelInstance {

  type ModelRunResult = Future[Either[ModelRunException, JMap[String, _]]]

  type ModelInputData = VectorMultimap[String, AnyRef]
}
