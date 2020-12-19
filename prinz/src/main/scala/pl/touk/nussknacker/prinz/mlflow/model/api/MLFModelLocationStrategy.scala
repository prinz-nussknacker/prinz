package pl.touk.nussknacker.prinz.mlflow.model.api

abstract sealed class MLFModelLocationStrategy {

  def createModelRelativeUrl(model: MLFRegisteredModel): String
}

object LocalMLFModelLocationStrategy extends MLFModelLocationStrategy {

  private val MODEL_NAME_SPLIT_BY: Char = '-'

  override def createModelRelativeUrl(model: MLFRegisteredModel): String = {
    val localModelBaseName = getLocalModelBaseName(model)
    s"/$localModelBaseName/invocations"
  }

  private def getLocalModelBaseName(model: MLFRegisteredModel): String = {
    val nameParts = model.name.name.split(MODEL_NAME_SPLIT_BY)
    s"${nameParts(0)}$MODEL_NAME_SPLIT_BY${nameParts(1)}"
  }
}

object AzureDataBricksMLFModelLocationStrategy extends MLFModelLocationStrategy {

  override def createModelRelativeUrl(model: MLFRegisteredModel): String = {
    s"/model/${model.name.name}/${model.getVersion.name}/invocations"
  }
}
