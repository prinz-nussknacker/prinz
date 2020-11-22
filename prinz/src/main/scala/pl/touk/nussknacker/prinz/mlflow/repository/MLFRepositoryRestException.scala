package pl.touk.nussknacker.prinz.mlflow.repository

import pl.touk.nussknacker.prinz.model.repository.ModelRepositoryException
import pl.touk.nussknacker.prinz.util.http.RestClientException

class MLFRepositoryRestException(message: String) extends ModelRepositoryException(message) {

  def this(ex: RestClientException) = this(ex.getMessage)
}
