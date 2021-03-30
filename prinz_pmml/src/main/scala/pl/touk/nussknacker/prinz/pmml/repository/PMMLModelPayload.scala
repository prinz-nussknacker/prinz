package pl.touk.nussknacker.prinz.pmml.repository

import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository.{NAME_VERSION_SEPARATOR, PMML_FILE_EXTENSION}

import java.io.InputStream

final case class PMMLModelPayload(inputStream: InputStream, name: String, version: String)

object PMMLModelPayload {

  def apply(inputStream: InputStream, filename: String): PMMLModelPayload = {
    val (name, version) = splitFilenameToNameAndVersion(filename)
    PMMLModelPayload(inputStream, name, version)
  }

  private def splitFilenameToNameAndVersion(filename: String): (String, String) = {
    val data = filename
      .dropRight(PMML_FILE_EXTENSION.length)
      .split(NAME_VERSION_SEPARATOR)
    if (data.length != 2) {
      throw new IllegalArgumentException(s"Invalid PMMLModel name: $filename. " +
        s"Name should finish with $PMML_FILE_EXTENSION extension " +
        s"and have a version tag $NAME_VERSION_SEPARATOR with following version number")
    }
    (data(0), data(1))
  }
}
