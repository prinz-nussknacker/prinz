package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestInvokeBody
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFInvokeRestClient
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException}

case class MLFModelInstance(config: MLFConfig, model: MLFRegisteredModel)
  extends ModelInstance(model, MLFSignatureInterpreter(config)) {

  private val restClient = MLFInvokeRestClient(config.servedModelsUrl.toString, model)

  override def run(columns: List[String], data: List[List[Double]]): ModelRunResult =
    restClient.invoke(MLFRestInvokeBody(columns, data), config.modelLocationStrategy)
      .map { response => response.left.map(e => new ModelRunException(e)) }
}
