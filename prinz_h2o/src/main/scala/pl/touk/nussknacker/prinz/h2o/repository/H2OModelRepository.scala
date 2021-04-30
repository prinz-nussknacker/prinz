package pl.touk.nussknacker.prinz.h2o.repository

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.h2o.H2OConfig
import pl.touk.nussknacker.prinz.h2o.model.H2OModel
import pl.touk.nussknacker.prinz.model.{Model, ModelName}
import pl.touk.nussknacker.prinz.model.repository.{ModelRepository}
import pl.touk.nussknacker.prinz.util.repository.{ModelPayload, RepositoryClient}

class H2OModelRepository(implicit config: H2OConfig)
  extends ModelRepository with LazyLogging {

  private val client = new RepositoryClient
  private val uri = config.modelsDirectory

  override def listModels: RepositoryResponse[List[H2OModel]] =
    client.listModelFiles(uri).right.map(it => it.map(p => new H2OModel(mapPayload(p))).toList)

  override def getModel(name: ModelName): RepositoryResponse[Model] = client.listModelFiles(uri)
    .right.map(it => it.map(mapPayload).filter(p => p.name == name.toString))
    .map(it => new H2OModel(it.head))

  private def mapPayload(payload: ModelPayload): H2OModelPayload =
    H2OModelPayload(payload, config.fileExtension)
}

object H2OModelRepository {

  //TODO: I think this also should be in config
  val NAME_VERSION_SEPARATOR: String = "-v"
}
