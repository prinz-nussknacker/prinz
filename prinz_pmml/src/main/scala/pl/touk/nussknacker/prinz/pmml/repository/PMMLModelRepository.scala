package pl.touk.nussknacker.prinz.pmml.repository

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Jsoup
import pl.touk.nussknacker.prinz.model.repository.{ModelRepository, ModelRepositoryException}
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}
import pl.touk.nussknacker.prinz.pmml.PMMLConfig
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository.PMML_FILE_EXTENSION

import java.io.{FileInputStream, InputStream}
import java.net.URL
import java.nio.file.Paths
import scala.collection.JavaConverters

class PMMLModelRepository(implicit val config: PMMLConfig) extends ModelRepository with LazyLogging {

  override def listModels: RepositoryResponse[List[PMMLTemporaryModel]] = {
    val url = config.modelsDirectory

    logger.info(s"Protocol used in PMMLModelRepository is ${url.getProtocol}")

    val files = url.getProtocol match {
      case "file" => readFileDataFromURL(url)
      case "http" => readHttpDataFromURL(url)
      case "https" => readHttpDataFromURL(url)
    }
    Right[ModelRepositoryException, List[PMMLTemporaryModel]](files.map(PMMLTemporaryModel).toList)
  }

  private def readFileDataFromURL(url: URL): Iterable[InputStream] = {
    val urlFile = Paths.get(url.toURI).toFile
    if (urlFile.isDirectory) {
      urlFile.listFiles()
        .filter(_.isFile)
        .filter(_.getName.endsWith(PMML_FILE_EXTENSION))
        .map(new FileInputStream(_))
    }
    else {
      val dataStream = new FileInputStream(urlFile)
      List(dataStream)
    }
  }

  private def readHttpDataFromURL(url: URL): Iterable[InputStream] = {
    val urlString = url.toString
    if (urlString.endsWith(PMML_FILE_EXTENSION)) {
      val dataStream = url.openStream()
      List(dataStream)
    }
    else {
      val doc = Jsoup.connect(urlString).get()
      val elements = doc
        .select(config.modelDirectoryHrefSelector)
        .eachAttr("href")
      JavaConverters.iterableAsScalaIterable(elements)
        .filter(_.endsWith(PMML_FILE_EXTENSION))
        .map(createURLForSingleFile(urlString))
        .map(_.openStream())
    }
  }

  private def createURLForSingleFile(urlString: String)(fileName: String): URL = {
    val fixedUrlString = if (urlString.endsWith("/")) urlString else s"$urlString/"
    new URL(s"$fixedUrlString$fileName")
  }

  override def getModel(name: ModelName): RepositoryResponse[PMMLTemporaryModel] = ???
}

object PMMLModelRepository {

  protected val PMML_FILE_EXTENSION: String = ".pmml"
}

// TODO create proper pmml API which would handle model input stream correctly
// so it should be probably Closeable in order to close these streams after
// reading models data
case class PMMLTemporaryModel(inputStream: InputStream) extends Model {

  override def getName: ModelName = ???

  override def getVersion: ModelVersion = ???

  override def toModelInstance: ModelInstance = ???
}
