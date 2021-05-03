package pl.touk.nussknacker.prinz.h2o.repository

import pl.touk.nussknacker.prinz.util.repository.payload.ModelPayload
import pl.touk.nussknacker.prinz.util.repository.payload.ModelPayload.splitFilenameToNameAndVersion

import java.net.URL

final case class H2OModelPayload(path: URL, name: String, version: String)

object H2OModelPayload {

  def apply(payload: ModelPayload, extension: String, separator: String): H2OModelPayload = {
    val (name, version) = splitFilenameToNameAndVersion(payload.filename, extension, separator)
    H2OModelPayload(payload.path.toURL, name, version)
  }
}
