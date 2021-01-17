package pl.touk.nussknacker.prinz.mlflow.model.api

abstract sealed class MLFModelLocationStrategy {

  def createModelRelativeUrl(model: MLFRegisteredModel): String
}

object LocalMLFModelLocationStrategy extends MLFModelLocationStrategy {

  private val MODEL_NAME_SPLIT_BY: Char = '-'

  private val BASENAME_MODEL_INDEX: Int = 0

  private val VERSION_MODEL_INDEX: Int = 1

  override def createModelRelativeUrl(model: MLFRegisteredModel): String = {
    val localModelBaseName = getLocalModelBaseName(model)
    s"/$localModelBaseName/invocations"
  }

  private def getLocalModelBaseName(model: MLFRegisteredModel): String = {
    val nameParts = model.name.name.split(MODEL_NAME_SPLIT_BY)
    s"${nameParts(BASENAME_MODEL_INDEX)}$MODEL_NAME_SPLIT_BY${nameParts(VERSION_MODEL_INDEX)}"
  }
}

object AzureDataBricksMLFModelLocationStrategy extends MLFModelLocationStrategy {

  override def createModelRelativeUrl(model: MLFRegisteredModel): String = {
    s"/model/${model.name.name}/${model.getVersion.name}/invocations"
  }
}
