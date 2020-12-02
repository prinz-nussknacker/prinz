package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.prinz.util.exceptions.PrinzException

case class MLFSignatureNotFoundException(model: MLFRegisteredModel)
  extends PrinzException(s"Cannot found MLFlow MLModel $model")
