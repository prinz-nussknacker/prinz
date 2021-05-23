package pl.touk.nussknacker.prinz.mlflow.repository

import pl.touk.nussknacker.prinz.model.repository.ModelRepositoryException
import pl.touk.nussknacker.prinz.util.http.RestClientException

class MLFRepositoryException(cause: Throwable) extends ModelRepositoryException(cause) {

  def this(ex: RestClientException) = this(ex)

  override def toString: String = s"${getClass.getSimpleName}: ${this.getMessage}"
}
