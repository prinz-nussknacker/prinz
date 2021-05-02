package pl.touk.nussknacker.prinz.util.repository.client

import pl.touk.nussknacker.prinz.model.repository.ModelRepositoryException
import pl.touk.nussknacker.prinz.util.repository.payload.ModelPayload

import java.io.InputStream
import java.net.URI

class RepositoryClientFactory(implicit config: RepositoryClientConfig) {

  def listModelFiles(path: URI): Either[ModelRepositoryException, Iterable[ModelPayload]] =
    getClient(path).listModelFiles(path)

  def openModelFile(path: URI): InputStream = getClient(path).openModelFile(path)

  private def getClient(path: URI): AbstractRepositoryClient = {
    path.getScheme match {
      case "http" => new HttpRepositoryClient(config.fileExtension, selector)
      case "file" => new LocalFSRepositoryClient(config.fileExtension)
      case _ => throw new IllegalArgumentException("Unsupported URI scheme in repository location")
    }
  }

  private val selector = config.modelDirectoryHrefSelector match {
    case Some(value) => value
    case None => throw new IllegalStateException("modelDirectoryHrefSelector should be defined when " +
      "using repository based on HTTP")
  }

}
