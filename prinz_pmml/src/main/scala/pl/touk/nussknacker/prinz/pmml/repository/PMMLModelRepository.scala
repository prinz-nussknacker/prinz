package pl.touk.nussknacker.prinz.pmml.repository

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Jsoup
import pl.touk.nussknacker.prinz.model.repository.{ModelRepository, ModelRepositoryException}
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}
import pl.touk.nussknacker.prinz.pmml.PMMLConfig
import pl.touk.nussknacker.prinz.pmml.model.PMMLModel
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository.{PMML_FILE_EXTENSION, isPMMLFile}

import java.io.{FileInputStream, InputStream}
import java.net.URL
import java.nio.file.Paths
import scala.collection.JavaConverters

abstract class PMMLModelRepository(implicit protected val config: PMMLConfig) extends ModelRepository with LazyLogging {

  override def listModels: RepositoryResponse[List[PMMLModel]] = {
    val url = config.modelsDirectory
    logger.info(s"Protocol used in PMMLModelRepository is ${url.getProtocol}")
    val files = readPMMFilesData(url, config)
    Right[ModelRepositoryException, List[PMMLModel]](files.map(PMMLModel(_)).toList)
  }

  abstract protected def readPMMFilesData(url: URL, config: PMMLConfig): Iterable[InputStream]

  override def getModel(name: ModelName): RepositoryResponse[PMMLModel] = ???

  protected def isPMMLFile(fileName: String): Boolean = fileName.endsWith(PMML_FILE_EXTENSION)
}

object PMMLModelRepository {

  protected val PMML_FILE_EXTENSION: String = ".pmml"
}

