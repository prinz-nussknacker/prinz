package pl.touk.nussknacker.prinz.h2o.repository

import pl.touk.nussknacker.prinz.h2o.repository.H2OModelRepository.{NAME_VERSION_SEPARATOR}
import pl.touk.nussknacker.prinz.util.repository.ModelPayload

import java.net.URL

final case class H2OModelPayload(path: URL, name: String, version: String)

object H2OModelPayload {

  def apply(payload: ModelPayload, extension: String): H2OModelPayload = {
    val (name, version) = splitFilenameToNameAndVersion(payload.filename, extension)
    H2OModelPayload(payload.path.toURL, name, version)
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