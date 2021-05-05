package pl.touk.nussknacker.prinz.h2o

import com.typesafe.config.Config
import pl.touk.nussknacker.prinz.util.repository.client.RepositoryClientConfig
import pl.touk.nussknacker.prinz.util.repository.payload.ModelVersionConfig

final case class H2OConfig(protected implicit val config: Config)
  extends RepositoryClientConfig
    with ModelVersionConfig {

  override protected implicit def baseConfigPath: String = "h2o."
}
