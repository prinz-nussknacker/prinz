package pl.touk.nussknacker.prinz.mlflow.model.api

abstract sealed class MLFModelLocationStrategy {

  def createModelRelativeUrl(model: MLFRegisteredModel): String

  def getModelExperimentId(model: MLFRegisteredModel): Int
}

object LocalMLFModelLocationStrategy extends MLFModelLocationStrategy {

  private val EXPERIMENT_ID_INDEX_IN_NAME: Int = 1

  private val MODEL_NAME_SPLIT_BY: Char = '-'

  override def createModelRelativeUrl(model: MLFRegisteredModel): String = {
    val experimentId = getModelExperimentId(model)
    s"/$experimentId/invocations"
  }

  override def getModelExperimentId(model: MLFRegisteredModel): Int =
    model.name.name.split(MODEL_NAME_SPLIT_BY)(EXPERIMENT_ID_INDEX_IN_NAME).toInt
}

object AzureDataBricksMLFModelLocationStrategy extends MLFModelLocationStrategy {

  override def createModelRelativeUrl(model: MLFRegisteredModel): String = {
    s"/model/${model.name.name}/${model.getVersion.name}/invocations"
  }

  override def getModelExperimentId(model: MLFRegisteredModel): Int =
    throw new NotImplementedError("Not implemented yet")
}
