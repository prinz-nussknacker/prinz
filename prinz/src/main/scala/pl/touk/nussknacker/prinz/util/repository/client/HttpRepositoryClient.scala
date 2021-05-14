package pl.touk.nussknacker.prinz.util.repository.client

import org.jsoup.Jsoup
import pl.touk.nussknacker.prinz.util.repository.payload.ModelPayload

import java.io.{File, InputStream}
import java.net.URI
import scala.collection.JavaConverters


class HttpRepositoryClient(path: URI, fileExtension: String, selector: String)
  extends AbstractRepositoryClient(fileExtension) {

  override protected def loadModelsOnPath: Iterable[ModelPayload] = {
    if(isValidFile(path)) {
      List(ModelPayload(path))
    }
    else {
      val pathString = path.toString
      try {
        val doc = Jsoup.connect(pathString).get
        val elements = doc.select(selector)
          .eachAttr("href")
        JavaConverters.iterableAsScalaIterable(elements)
          .map(createURIForSingleFile(pathString))
          .filter(isValidFile)
          .map(ModelPayload(_))
      }
      catch {
        case ex: Exception => throw ex
      }
    }
  }

  override def openModelFile(path: URI): InputStream = path.toURL.openStream()

  private def createURIForSingleFile(urlString: String)(fileName: String): URI = {
    val fixedUrlString = if (urlString.endsWith(File.separator)) urlString else s"$urlString/"
    new URI(s"$fixedUrlString$fileName")
  }
}
