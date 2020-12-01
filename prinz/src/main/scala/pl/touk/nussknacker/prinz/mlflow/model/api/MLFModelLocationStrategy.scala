package pl.touk.nussknacker.prinz.mlflow.model.api

abstract sealed class MLFModelLocationStrategy {

  def createModelRelativeUrl(model: MLFRegisteredModel): String
}

object LocalMLFModelLocation extends MLFModelLocationStrategy {

  override def createModelRelativeUrl(model: MLFRegisteredModel): String = "/invocations"
}
