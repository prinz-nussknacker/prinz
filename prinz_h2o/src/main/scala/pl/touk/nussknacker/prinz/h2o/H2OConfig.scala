package pl.touk.nussknacker.prinz.h2o

import com.typesafe.config.Config
import pl.touk.nussknacker.prinz.util.repository.client.RepositoryClientConfig
import pl.touk.nussknacker.prinz.util.repository.payload.ModelVersionConfig

class H2OConfig(protected implicit val config: Config) extends RepositoryClientConfig with ModelVersionConfig {

  override protected implicit def BASE_CONFIG_PATH: String = "h2o."
}
