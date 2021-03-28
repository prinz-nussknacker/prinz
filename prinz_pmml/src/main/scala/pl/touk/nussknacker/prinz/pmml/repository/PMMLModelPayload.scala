package pl.touk.nussknacker.prinz.pmml.repository

import java.io.InputStream

final case class PMMLModelPayload(inputStream: InputStream, name: String, version: String)

object PMMLModelPayload {
  def apply(inputStream: InputStream, filename: String): PMMLModelPayload = {
    val data = filename.dropRight(PMMLModelRepository.PMML_FILE_EXTENSION.length)
      .split(PMMLModelRepository.NAME_VERSION_SEPARATOR)
    PMMLModelPayload(inputStream, data(0), data(1))
  }
}
