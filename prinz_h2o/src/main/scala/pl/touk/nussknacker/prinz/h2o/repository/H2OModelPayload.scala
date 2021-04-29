package pl.touk.nussknacker.prinz.h2o.repository

import pl.touk.nussknacker.prinz.h2o.repository.H2OModelRepository.{H2O_FILE_EXTENSION, NAME_VERSION_SEPARATOR}
import pl.touk.nussknacker.prinz.util.repository.ModelPayload

import java.net.URL

final case class H2OModelPayload(path: URL, name: String, version: String)

object H2OModelPayload {

  def apply(payload: ModelPayload): H2OModelPayload = {
    val (name, version) = splitFilenameToNameAndVersion(payload.filename)
    H2OModelPayload(payload.path.toURL, name, version)
  }

  private def splitFilenameToNameAndVersion(filename: String): (String, String) = {
    val data = filename
      .dropRight(H2O_FILE_EXTENSION.length)
      .split(NAME_VERSION_SEPARATOR)
    if (data.length != 2) {
      throw new IllegalArgumentException(s"Invalid PMMLModel name: $filename. " +
        s"Name should finish with $H2O_FILE_EXTENSION extension " +
        s"and have a version tag $NAME_VERSION_SEPARATOR with following version number")
    }
    (data(0), data(1))
  }
}