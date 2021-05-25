package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.exceptions.PrinzException

case class SignatureNotFoundException(exception: Exception)
  extends PrinzException(s"Cannot find signature in SignatureProvider: $exception", exception)
