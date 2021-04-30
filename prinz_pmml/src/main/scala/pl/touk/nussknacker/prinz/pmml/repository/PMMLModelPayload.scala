package pl.touk.nussknacker.prinz.pmml.repository

import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository.{NAME_VERSION_SEPARATOR}
import pl.touk.nussknacker.prinz.util.repository.{ModelPayload}

import java.io.InputStream

final case class PMMLModelPayload(inputStream: InputStream, name: String, version: String)

object PMMLModelPayload {

  def apply(payload: ModelPayload, inputStream: InputStream, extension: String): PMMLModelPayload = {
    val (name, version) = splitFilenameToNameAndVersion(payload.filename, extension)
    PMMLModelPayload(inputStream, name, version)
  }

  private def splitFilenameToNameAndVersion(filename: String, extension: String): (String, String) = {
    val data = filename
      .dropRight(extension.length)
      .split(NAME_VERSION_SEPARATOR)
    if (data.length != 2) {
      throw new IllegalArgumentException(s"Invalid PMMLModel name: $filename. " +
        s"Name should finish with $extension extension " +
        s"and have a version tag $NAME_VERSION_SEPARATOR with following version number")
    }
    (data(0), data(1))
  }
}
