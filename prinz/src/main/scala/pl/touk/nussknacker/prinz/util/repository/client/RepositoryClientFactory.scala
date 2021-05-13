package pl.touk.nussknacker.prinz.util.repository.client

class RepositoryClientFactory(implicit config: RepositoryClientConfig) {

  def getClient: AbstractRepositoryClient = client

  private val path = config.modelsDirectory

  private def getHrefSelector = config.modelDirectoryHrefSelector match {
    case Some(value) => value
    case None => throw new IllegalStateException("modelDirectoryHrefSelector should be defined when " +
      "using repository based on HTTP")
  }

  private val client: AbstractRepositoryClient = path.getScheme match {
    case "http" => new HttpRepositoryClient(path, config.fileExtension, getHrefSelector)
    case "file" => new LocalFSRepositoryClient(path, config.fileExtension)
    case _ => throw new IllegalArgumentException("Unsupported URI scheme in repository location")
  }
}
