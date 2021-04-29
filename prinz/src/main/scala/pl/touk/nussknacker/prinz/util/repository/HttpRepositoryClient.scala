package pl.touk.nussknacker.prinz.util.repository

import java.net.{URI, URL}
import org.jsoup.Jsoup

import java.io.{File, InputStream}
import scala.collection.JavaConverters


class HttpRepositoryClient(fileExtension: String) extends AbstractRepositoryClient(fileExtension) {

  override protected def readPath(path: URI): Iterable[ModelPayload] = {
    if(isCorrectFile(path)) {
      List(ModelPayload(path))
    }
    else {
      val pathString = path.toString
      val doc = Jsoup.connect(pathString).get
      val elements = doc.select(SELECTOR_TMP)
        .eachAttr("href")
      JavaConverters.iterableAsScalaIterable(elements)
        .map(createURIForSingleFile(pathString))
        .filter(isCorrectFile)
        .map(f => ModelPayload(f))
    }
  }

  override def openModelFile(path: URI): InputStream = path.toURL.openStream()

  private def createURIForSingleFile(urlString: String)(fileName: String): URI = {
    val fixedUrlString = if (urlString.endsWith(File.separator)) urlString else s"$urlString/"
    new URI(s"$fixedUrlString$fileName")
  }

  private val SELECTOR_TMP = "body > ul > li > a"
}
