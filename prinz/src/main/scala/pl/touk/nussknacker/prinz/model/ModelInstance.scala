package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import java.util.{Map => JMap}
import scala.concurrent.Future

abstract class ModelInstance(val model: Model) {

  protected def verify(inputMap: ModelInputData): Either[ModelRunException, ModelInputData] = {
    val inputColumnNames = inputMap.keys.toSet
    val signature = model.getMetadata.signature
    val signatureColumnNames = signature.getSignatureInputs.map(_.signatureName.name).toSet
    if (inputColumnNames.equals(signatureColumnNames)) {
      Right(inputMap)
    }
    else {
      Left(new ModelRunException(s"Input data $inputMap does not match signature for " +
        s"model ${model.getMetadata.modelName} with signature $signature"))
    }
  }

  protected def runVerified(inputMap: ModelInputData): ModelRunResult

  final def run(inputMap: ModelInputData): ModelRunResult = verify(inputMap) match {
    case Right(verifiedInputMap) => runVerified(verifiedInputMap)
    case Left(value) => Future(Left(value))
  }

  override def toString: String = s"ModelInstance for: $model"
}

object ModelInstance {

  type ModelRunResult = Future[Either[ModelRunException, JMap[String, _]]]

  type ModelInputData = VectorMultimap[String, AnyRef]
}
