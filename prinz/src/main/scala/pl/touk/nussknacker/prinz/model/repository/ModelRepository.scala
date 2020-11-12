package pl.touk.nussknacker.prinz.model.repository

import pl.touk.nussknacker.prinz.model.Model

trait ModelRepository {
  def listModels(): Either[Exception, List[Model]]
  def getModel(key: String): Either[Exception, Model]
}
