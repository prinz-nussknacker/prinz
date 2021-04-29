package pl.touk.nussknacker.prinz.util.repository

import java.io.InputStream
import java.net.URI

abstract class AbstractRepositoryClient(fileExtension: String) {

  def listModelFiles(path: URI): Iterable[ModelPayload] = readPath(path)

  def openModelFile(path: URI): InputStream

  protected def readPath(path: URI): Iterable[ModelPayload]

  protected def isCorrectFile(path: URI): Boolean = path.toString.endsWith(fileExtension)
}
