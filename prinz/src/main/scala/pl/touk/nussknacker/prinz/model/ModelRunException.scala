package pl.touk.nussknacker.prinz.model

import pl.touk.nussknacker.prinz.util.exceptions.PrinzException
import pl.touk.nussknacker.prinz.util.http.RestClientException

class ModelRunException(message: String) extends PrinzException(message) {

  def this(e: Exception) = this(e.getMessage)
}
