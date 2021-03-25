package pl.touk.nussknacker.prinz.pmml.repository

import pl.touk.nussknacker.prinz.pmml.PMMLConfig
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository.PMML_FILE_EXTENSION

import java.io.{FileInputStream, InputStream}
import java.net.URL
import java.nio.file.Paths

class LocalFilePMMLModelRepository(implicit config: PMMLConfig) extends PMMLModelRepository {

  override protected def readPMMFilesData(url: URL, config: PMMLConfig): Iterable[PMMLModelPayload] = {
    val urlFile = Paths.get(url.toURI).toFile
    if (urlFile.isDirectory) {
      urlFile.listFiles()
        .filter(_.isFile)
        .filter(file => isPMMLFile(file.getName))
        .map(file => PMMLModelPayload(new FileInputStream(file), file.getName))
    }
    else {
      val dataStream = new FileInputStream(urlFile)
      List(PMMLModelPayload(dataStream, urlFile.getName))
    }
  }
}
