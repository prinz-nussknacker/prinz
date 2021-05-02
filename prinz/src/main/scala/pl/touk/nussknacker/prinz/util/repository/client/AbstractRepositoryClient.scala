package pl.touk.nussknacker.prinz.util.repository.client

import pl.touk.nussknacker.prinz.model.repository.ModelRepositoryException
import pl.touk.nussknacker.prinz.util.repository.payload.ModelPayload

import java.io.InputStream
import java.net.URI

abstract class AbstractRepositoryClient(fileExtension: String) {

//  def listModelFiles(path: URI): Iterable[ModelPayload] = readPath(path)

  def listModelFiles(path: URI): Either[ModelRepositoryException, Iterable[ModelPayload]] = try {
    Right(loadModelsOnPath(path))
  } catch {
    case ex: Exception => Left(new ModelRepositoryException(ex.getMessage))
  }


  def openModelFile(path: URI): InputStream

  protected def loadModelsOnPath(path: URI): Iterable[ModelPayload]

  protected def isValidFile(path: URI): Boolean = path.toString.endsWith(fileExtension)
}