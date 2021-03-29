package pl.touk.nussknacker.prinz.pmml.repository

import org.jsoup.Jsoup
import pl.touk.nussknacker.prinz.pmml.PMMLConfig

import java.io.File
import java.net.URL
import scala.collection.JavaConverters

class HttpPMMLModelRepository(implicit config: PMMLConfig) extends PMMLModelRepository {

  override protected def readPMMFilesData(url: URL, config: PMMLConfig): Iterable[PMMLModelPayload] = {
    val urlString = url.toString

    if (isPMMLFile(urlString)) {
      val dataStream = url.openStream()
      List(PMMLModelPayload(dataStream, urlString.split(File.separator).last))
    }
    else {
      val doc = Jsoup.connect(urlString).get()
      val elements = doc
        .select(config.modelDirectoryHrefSelector)
        .eachAttr("href")
      JavaConverters.iterableAsScalaIterable(elements)
        .filter(isPMMLFile)
        .map(generatePayload(urlString))
    }
  }

  private def createURLForSingleFile(urlString: String, fileName: String): URL = {
    val fixedUrlString = if (urlString.endsWith(File.separator)) urlString else s"$urlString/"
    new URL(s"$fixedUrlString$fileName")
  }

  private def generatePayload(urlString: String)(file: String): PMMLModelPayload =
    PMMLModelPayload(createURLForSingleFile(urlString, file).openStream, file)
}
