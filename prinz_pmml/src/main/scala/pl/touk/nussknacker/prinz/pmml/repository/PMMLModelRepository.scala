package pl.touk.nussknacker.prinz.pmml.repository

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.model.repository.{ModelRepository, ModelRepositoryException}
import pl.touk.nussknacker.prinz.model.ModelName
import pl.touk.nussknacker.prinz.pmml.PMMLConfig
import pl.touk.nussknacker.prinz.pmml.model.PMMLModel
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository.PMML_FILE_EXTENSION
import pl.touk.nussknacker.prinz.util.repository.{ModelPayload, RepositoryClient}

import java.net.URL


class PMMLModelRepository(implicit config: PMMLConfig)
  extends ModelRepository with LazyLogging {

  private val client = new RepositoryClient(PMML_FILE_EXTENSION)
  private val uri = config.modelsDirectory

  override def listModels: RepositoryResponse[List[PMMLModel]] =
    Right[ModelRepositoryException, List[PMMLModel]](client.listModelFiles(uri)
      .map(p => PMMLModel(mapPayload(p))).toList)

  override def getModel(name: ModelName): RepositoryResponse[PMMLModel] =
    Right[ModelRepositoryException, PMMLModel](PMMLModel(client.listModelFiles(uri).map(mapPayload)
      .filter(p => p.name == name.toString).head))

  private def mapPayload(payload: ModelPayload): PMMLModelPayload =
    PMMLModelPayload(payload, client.openModelFile(payload.path))

//  private def readURL: Iterable[PMMLModelPayload] = {
//    val url = config.modelsDirectory
//    logger.info(s"Protocol used in PMMLModelRepository is ${url.getProtocol}")
//    readPMMFilesData(url, config)
//  }

//  protected def readPMMFilesData(url: URL, config: PMMLConfig): Iterable[PMMLModelPayload]
//
//  protected def isPMMLFile(fileName: String): Boolean = fileName.endsWith(PMML_FILE_EXTENSION)
}

object PMMLModelRepository {

  val NAME_VERSION_SEPARATOR: String = "-v"

  val PMML_FILE_EXTENSION: String = ".pmml"
}
