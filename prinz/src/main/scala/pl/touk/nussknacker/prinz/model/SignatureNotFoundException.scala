package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.exceptions.PrinzException

case class SignatureNotFoundException(model: Model)
  extends PrinzException(s"Cannot find signature for $model")
