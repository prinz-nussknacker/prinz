package pl.touk.nussknacker.prinz.util.repository

import java.net.URI
import java.nio.file.{Path, Paths}
import scala.reflect.io.File

case class ModelPayload(path: URI, filename: String)

object ModelPayload {
  def apply(path: URI): ModelPayload = ModelPayload(path, extractFilename(path))

  private def extractFilename(path: URI): String =
    path.getSchemeSpecificPart.split(File.separator).last
}
