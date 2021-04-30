package pl.touk.nussknacker.prinz.pmml.repository

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.model.repository.{ModelRepository}
import pl.touk.nussknacker.prinz.model.ModelName
import pl.touk.nussknacker.prinz.pmml.PMMLConfig
import pl.touk.nussknacker.prinz.pmml.model.PMMLModel
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository.PMML_FILE_EXTENSION
import pl.touk.nussknacker.prinz.util.repository.{ModelPayload, RepositoryClient}

class PMMLModelRepository(implicit config: PMMLConfig)
  extends ModelRepository with LazyLogging {

  private val client = new RepositoryClient(PMML_FILE_EXTENSION)
  private val uri = config.modelsDirectory

  override def listModels: RepositoryResponse[List[PMMLModel]] =
    client.listModelFiles(uri).right.map(it => it.map(p => PMMLModel(mapPayload(p))).toList)

  override def getModel(name: ModelName): RepositoryResponse[PMMLModel] =
    client.listModelFiles(uri)
      .right.map(it => it.map(mapPayload).filter(p => p.name == name.toString))
      .map(it => PMMLModel(it.head))

  private def mapPayload(payload: ModelPayload): PMMLModelPayload =
    PMMLModelPayload(payload, client.openModelFile(payload.path))
}

object PMMLModelRepository {

  val NAME_VERSION_SEPARATOR: String = "-v"

  val PMML_FILE_EXTENSION: String = ".pmml"
}
