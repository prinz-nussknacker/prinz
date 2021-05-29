package pl.touk.nussknacker.prinz.model.repository

import pl.touk.nussknacker.prinz.model.{Model, ModelName}

trait ModelRepository {

  type RepositoryResponse[RESPONSE] = Either[ModelRepositoryException, RESPONSE]

  def name: ModelRepositoryName = ModelRepositoryName(getClass.getSimpleName)

  def listModels: RepositoryResponse[List[Model]]
}

final case class ModelRepositoryName(internal: String) {
  override def toString: String = internal
}
