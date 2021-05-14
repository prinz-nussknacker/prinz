package pl.touk.nussknacker.prinz.pmml.repository

import pl.touk.nussknacker.prinz.util.repository.payload.ModelPayload
import pl.touk.nussknacker.prinz.util.repository.payload.ModelPayload.splitFilenameToNameAndVersion

import java.io.InputStream

final case class PMMLModelPayload(inputStreamSource: () => InputStream, name: String, version: String)

object PMMLModelPayload {

  def apply(payload: ModelPayload, inputStreamSource: () => InputStream, extension: String, separator: String): PMMLModelPayload = {
    val (name, version) = splitFilenameToNameAndVersion(payload.filename, extension, separator)
    PMMLModelPayload(inputStreamSource, name, version)
  }
}
