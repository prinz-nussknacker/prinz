package pl.touk.nussknacker.prinz.h2o.repository

import pl.touk.nussknacker.prinz.h2o.H2OConfig
import pl.touk.nussknacker.prinz.h2o.model.H2OModel
import pl.touk.nussknacker.prinz.model.{Model, ModelName}
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.util.repository.client.{RepositoryClient}
import pl.touk.nussknacker.prinz.util.repository.payload.ModelPayload

class H2OModelRepository(implicit val config: H2OConfig)
  extends ModelRepository with RepositoryClient {

  override def listModels: RepositoryResponse[List[H2OModel]] =
    client.listModelFiles.right.map(it => it.map(p => new H2OModel(mapPayload(p))).toList)

  override def getModel(name: ModelName): RepositoryResponse[Model] = client.listModelFiles
    .right.map(it => it.map(mapPayload).filter(p => p.name == name.toString))
    .map(it => new H2OModel(it.head))

  private def mapPayload(payload: ModelPayload): H2OModelPayload =
    H2OModelPayload(payload, config.fileExtension, config.modelVersionSeparator)
}
