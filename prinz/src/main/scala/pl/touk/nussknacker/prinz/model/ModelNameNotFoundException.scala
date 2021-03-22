package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.exceptions.PrinzException

case class ModelNameNotFoundException()
  extends PrinzException("Model name not found")
