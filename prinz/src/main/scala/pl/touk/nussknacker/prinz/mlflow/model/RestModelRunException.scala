package pl.touk.nussknacker.prinz.mlflow.model

import pl.touk.nussknacker.prinz.model.ModelRunException
import pl.touk.nussknacker.prinz.util.http.RestClientException

class RestModelRunException(message: String) extends ModelRunException(message) {
  def this(ex: RestClientException) {
    this(ex.getMessage)
  }
}
