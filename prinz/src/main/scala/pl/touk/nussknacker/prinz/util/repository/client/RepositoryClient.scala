package pl.touk.nussknacker.prinz.util.repository.client

trait RepositoryClient {

  protected implicit val config: RepositoryClientConfig

  protected final val client = new RepositoryClientFactory().getClient
}
