package pl.touk.nussknacker.prinz.pmml.repository

import org.jsoup.Jsoup
import pl.touk.nussknacker.prinz.pmml.PMMLConfig

import java.io.InputStream
import java.net.URL
import scala.collection.JavaConverters

class HttpPMMLModelRepository(modelDirectoryHrefSelector: String)
                             (implicit config: PMMLConfig) extends PMMLModelRepository {

  override protected def readPMMFilesData(url: URL, config: PMMLConfig): Iterable[InputStream] = {
    val urlString = url.toString
    if (isPMMLFile(urlString)) {
      val dataStream = url.openStream()
      List(dataStream)
    }
    else {
      val doc = Jsoup.connect(urlString).get()
      val elements = doc
        .select(modelDirectoryHrefSelector)
        .eachAttr("href")
      JavaConverters.iterableAsScalaIterable(elements)
        .filter(isPMMLFile)
        .map(createURLForSingleFile(urlString))
        .map(_.openStream())
    }
  }

  private def createURLForSingleFile(urlString: String)(fileName: String): URL = {
    val fixedUrlString = if (urlString.endsWith("/")) urlString else s"$urlString/"
    new URL(s"$fixedUrlString$fileName")
  }
}
