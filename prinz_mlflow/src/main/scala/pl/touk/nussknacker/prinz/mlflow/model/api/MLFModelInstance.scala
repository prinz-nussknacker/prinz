package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestInvokeBody
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFInvokeRestClient
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException, SignatureName}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

case class MLFModelInstance(config: MLFConfig, model: MLFRegisteredModel)
  extends ModelInstance(model, MLFSignatureInterpreter(config)) {

  private val restClient = MLFInvokeRestClient(config.servedModelsUrl.toString, model)

  override def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult = {
    if(!validateInput(inputMap)) {
      Future(Left(new InvalidInputModelRunException("Mismatched input types.")))
    }
    else {
      //TODO: Here convert input map to object transformable to JSON
      restClient.invoke(MLFRestInvokeBody(columns, data), config.modelLocationStrategy)
        .map { response => response.left.map(e => new ModelRunException(e)) }
    }
  }

  def validateInput(input: VectorMultimap[String, AnyRef]): Boolean = {
    val interpreter = MLFSignatureInterpreter(config)
    val signature = getSignature
    var result = true

    input.takeWhile(_ => result).foreach(el => {
      val(k, v) = el
      val require = signature.getInputValueType(SignatureName(k))
      val given = interpreter.fromMLFDataType(v.getClass.toString)

      if(require.isEmpty) {
        result = false
      }

      else if(!given.canBeSubclassOf(require.get.typingResult)) {
        result = false
      }
    })

    result
  }
}
