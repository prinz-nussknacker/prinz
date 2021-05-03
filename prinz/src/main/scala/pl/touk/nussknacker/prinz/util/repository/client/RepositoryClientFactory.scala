package pl.touk.nussknacker.prinz.util.repository.client

class RepositoryClientFactory(implicit config: RepositoryClientConfig) {

  def getClient: AbstractRepositoryClient = client

  private val path = config.modelsDirectory

  private val client: AbstractRepositoryClient = path.getScheme match {
      case "http" => new HttpRepositoryClient(path, config.fileExtension, selector)
      case "file" => new LocalFSRepositoryClient(path, config.fileExtension)
      case _ => throw new IllegalArgumentException("Unsupported URI scheme in repository location")
  }

  private val selector = config.modelDirectoryHrefSelector match {
    case Some(value) => value
    case None => throw new IllegalStateException("modelDirectoryHrefSelector should be defined when " +
      "using repository based on HTTP")
  }
}
