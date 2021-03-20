package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.exceptions.PrinzException

case class ModelNotValidException(model: Model, ex: Exception)
  extends PrinzException(s"Model ${model.getName} not valid: ${ex.toString}")
