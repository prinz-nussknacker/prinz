package pl.touk.nussknacker.prinz.mlflow.model.api

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.converter.MLFDataConverter
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestInvokeBody
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFInvokeRestClient
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException, SignatureName}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

case class MLFModelInstance(config: MLFConfig, model: MLFRegisteredModel)
  extends ModelInstance(model, MLFSignatureInterpreter(config)) with LazyLogging {

  private val restClient = MLFInvokeRestClient(config.servedModelsUrl.toString, model)

  private val mlfSignatureInterpreter = MLFSignatureInterpreter(config)

  override def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult = {
    logger.info("JAAAAA JEBE ;----;")
    if(!validateInput(inputMap)) {
      Future(Left(new InvalidInputModelRunException("Mismatched input types.")))
    }
    else {
      logger.info("------------------RUN----------------")
      //TODO: Here convert input map to object transformable to JSON
      val result = restClient.invoke(MLFRestInvokeBody(MLFDataConverter.toJsonString(inputMap, getSignature)), config.modelLocationStrategy)
        .map { response => response.left.map(e => new ModelRunException(e)) }
      result
    }
  }

  def validateInput(input: VectorMultimap[String, AnyRef]): Boolean = {
    val signature = getSignature
    var result = true

    input.takeWhile(_ => result).foreach(el => {
      val(k, v) = el
      val require = signature.getInputValueType(SignatureName(k)).
      val given = mlfSignatureInterpreter.fromMLFDataType(v.getClass.toString.to)

      if(require.isEmpty) {
        result = false
      }

      else if(!given.canBeSubclassOf(require.get.typingResult.)) {
        result = false
      }
    })

    result
  }
}
