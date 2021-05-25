package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.exceptions.PrinzException

final case class ModelNotValidException(ex: Exception)
  extends PrinzException(s"Model not valid: ${ex.toString}", ex)
