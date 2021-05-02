package pl.touk.nussknacker.prinz.pmml

import com.typesafe.config.Config
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getOptionConfigValue, getString, getUri, getUrl}
import pl.touk.nussknacker.prinz.util.repository.client.RepositoryClientConfig
import pl.touk.nussknacker.prinz.util.repository.payload.ModelVersionConfig

import java.net.{URI, URL}

final case class PMMLConfig(override protected implicit val config: Config) extends RepositoryClientConfig
  with ModelVersionConfig {

  override protected implicit def baseConfigPath: String = "pmml."
}
