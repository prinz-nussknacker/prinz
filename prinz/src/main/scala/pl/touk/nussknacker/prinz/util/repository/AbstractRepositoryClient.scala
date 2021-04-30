package pl.touk.nussknacker.prinz.util.repository

import pl.touk.nussknacker.prinz.model.repository.{ModelRepository, ModelRepositoryException}

import java.io.InputStream
import java.net.URI

abstract class AbstractRepositoryClient(fileExtension: String) {

//  def listModelFiles(path: URI): Iterable[ModelPayload] = readPath(path)

  def listModelFiles(path: URI): Either[ModelRepositoryException, Iterable[ModelPayload]] = try {
    Right(readPath(path))
  } catch {
    case ex: Exception => Left(new ModelRepositoryException(ex.getMessage))
  }


  def openModelFile(path: URI): InputStream

  protected def readPath(path: URI): Iterable[ModelPayload]

  protected def isCorrectFile(path: URI): Boolean = path.toString.endsWith(fileExtension)
}
