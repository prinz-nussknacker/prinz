package pl.touk.nussknacker.prinz.util.repository

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.model.repository.ModelRepositoryException

import java.io.InputStream
import java.net.URI

class RepositoryClient(fileExtension: String) {

  def listModelFiles(path: URI): Either[ModelRepositoryException, Iterable[ModelPayload]] =
    getClient(path).listModelFiles(path)

  def openModelFile(path: URI): InputStream = getClient(path).openModelFile(path)

  private def getClient(path: URI): AbstractRepositoryClient = {
    path.getScheme match {
      case "http" => new HttpRepositoryClient(fileExtension)
      case "file" => new LocalFSRepositoryClient(fileExtension)
      case _ => throw new IllegalArgumentException("Unsupported repository type.")
    }
  }

}
