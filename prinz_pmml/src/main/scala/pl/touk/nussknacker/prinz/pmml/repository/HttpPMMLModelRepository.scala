package pl.touk.nussknacker.prinz.pmml.repository

import org.jsoup.Jsoup
import pl.touk.nussknacker.prinz.pmml.PMMLConfig

import java.net.URL
import scala.collection.JavaConverters

class HttpPMMLModelRepository(modelDirectoryHrefSelector: String)
                             (implicit config: PMMLConfig) extends PMMLModelRepository {

  override protected def readPMMFilesData(url: URL, config: PMMLConfig): Iterable[PMMLModelPayload] = {
    val urlString = url.toString

    if (isPMMLFile(urlString)) {
      val dataStream = url.openStream()
      List(PMMLModelPayload(dataStream, urlString.split("/").last))
    }
    else {
      val doc = Jsoup.connect(urlString).get()
      val elements = doc
        .select(modelDirectoryHrefSelector)
        .eachAttr("href")
      JavaConverters.iterableAsScalaIterable(elements)
        .filter(isPMMLFile)
        .map(file => (createURLForSingleFile(urlString, file), file))
        .map(payload => PMMLModelPayload(payload._1.openStream(), payload._2))
    }
  }

  private def createURLForSingleFile(urlString: String, fileName: String): URL = {
    val fixedUrlString = if (urlString.endsWith("/")) urlString else s"$urlString/"
    new URL(s"$fixedUrlString$fileName")
  }
}
