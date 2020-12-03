package pl.touk.nussknacker.prinz.util.http

import pl.touk.nussknacker.prinz.util.exceptions.PrinzException

case class RestClientException(message: String) extends PrinzException(message)
