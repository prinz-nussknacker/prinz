package pl.touk.nussknacker.prinz.mlflow.model.api

abstract sealed class MLFModelLocationStrategy {

  def createModelRelativeUrl(model: MLFRegisteredModel): String
}

object LocalMLFModelLocationStrategy extends MLFModelLocationStrategy {

  override def createModelRelativeUrl(model: MLFRegisteredModel): String = {
    val localModelBaseName = model.name.internal
    s"/$localModelBaseName/invocations"
  }
}

object AzureDataBricksMLFModelLocationStrategy extends MLFModelLocationStrategy {

  override def createModelRelativeUrl(model: MLFRegisteredModel): String = {
    s"/model/${model.name.name}/${model.getVersion.name}/invocations"
  }
}
