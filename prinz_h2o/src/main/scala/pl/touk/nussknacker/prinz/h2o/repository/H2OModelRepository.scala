package pl.touk.nussknacker.prinz.h2o.repository

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.h2o.H2OConfig
import pl.touk.nussknacker.prinz.h2o.model.H2OModel
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelRepository.H2O_FILE_EXTENSION
import pl.touk.nussknacker.prinz.model.{Model, ModelName}
import pl.touk.nussknacker.prinz.model.repository.{ModelRepository, ModelRepositoryException}
import pl.touk.nussknacker.prinz.util.repository.{ModelPayload, RepositoryClient}

class H2OModelRepository(implicit config: H2OConfig)
  extends ModelRepository with LazyLogging {

  private val client = new RepositoryClient(H2O_FILE_EXTENSION)
  private val uri = config.modelsDirectory

  override def listModels: RepositoryResponse[List[H2OModel]] =
    Right[ModelRepositoryException, List[H2OModel]](client.listModelFiles(uri)
    .map(p => new H2OModel(mapPayload(p))).toList)

  override def getModel(name: ModelName): RepositoryResponse[Model] =
    Right[ModelRepositoryException, H2OModel](new H2OModel(client.listModelFiles(uri).map(mapPayload)
    .filter(p => p.name == name.toString).head))

  private def mapPayload(payload: ModelPayload): H2OModelPayload =
    H2OModelPayload(payload)
}

object H2OModelRepository {

  val NAME_VERSION_SEPARATOR: String = "-v"

  val H2O_FILE_EXTENSION: String = ".lol idk"
}