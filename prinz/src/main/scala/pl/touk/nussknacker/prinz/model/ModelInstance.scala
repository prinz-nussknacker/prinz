package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap
import pl.touk.nussknacker.prinz.model.ModelRunException

import java.util.{Map => JMap}
import scala.concurrent.Future

abstract class ModelInstance(val model: Model) {

  protected def verify(inputMap: ModelInputData): Boolean = {
    val inputColumnNames = inputMap.keys.toSet
    val signatureColumnNames = model.getMetadata.signature.getInputNames.map(_.name).toSet
    inputColumnNames.equals(signatureColumnNames)
  }

  protected def runVerified(inputMap: ModelInputData): ModelRunResult

  final def run(inputMap: ModelInputData): ModelRunResult = {
    if(verify(inputMap)) {
      runVerified(inputMap)
    }
    else {
      throw new ModelRunException(s"Input data does not match signature for model ${model.getMetadata.modelName}")
    }
  }

  override def toString: String = s"ModelInstance for: $model"
}

object ModelInstance {

  type ModelRunResult = Future[Either[ModelRunException, JMap[String, _]]]

  type ModelInputData = VectorMultimap[String, AnyRef]
}
