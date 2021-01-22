package pl.touk.nussknacker.prinz.mlflow.model.api

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.converter.MLFDataConverter
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFInvokeRestClient
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException, SignatureName}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

case class MLFModelInstance(config: MLFConfig, model: MLFRegisteredModel)
  extends ModelInstance(model, MLFSignatureProvider(config)) with LazyLogging {

  private val invokeRestClient = MLFInvokeRestClient(config.servedModelsUrl.toString, model)

  override def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult = {
    val dataframe = MLFDataConverter.inputToDataframe(inputMap, getSignature)
    logger.info("Send dataframe to mlflow model: {}", dataframe)
    invokeRestClient.invoke(dataframe, getSignature, config.modelLocationStrategy)
      .map { response =>
        logger.info("Response from mlflow model: {}", response)
        response
          .left.map(exception => new ModelRunException(exception))
          .right.map(output => MLFDataConverter.outputToResultMap(output, getSignature))
      }
  }
}
