package pl.touk.nussknacker.prinz.util.repository.payload

import java.net.URI
import scala.reflect.io.File

case class ModelPayload(path: URI, filename: String)

object ModelPayload {
  def apply(path: URI): ModelPayload = ModelPayload(path, extractFilename(path))

  private def extractFilename(path: URI): String =
    path.getSchemeSpecificPart.split(File.separator).last

  def splitFilenameToNameAndVersion(filename: String, extension: String, separator: String): (String, String) = {
    val data = filename
      .dropRight(extension.length)
      .split(separator)
    if (data.length != 2) {
      throw new IllegalArgumentException(s"Invalid Model name: $filename. " +
        s"Name should finish with $extension extension " +
        s"and have a version tag $separator with following version number")
    }
    (data(0), data(1))
  }
}
