package pl.touk.nussknacker.prinz.model.repository

import pl.touk.nussknacker.prinz.model.Model

trait ModelRepository {

  type RepositoryResponse[RESPONSE] = Either[ModelRepositoryException, RESPONSE]

  def listModels: RepositoryResponse[List[Model]]
}
