package pl.touk.nussknacker.prinz.h2o

import com.typesafe.config.Config
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getOptionConfigValue, getString, getUri}
import pl.touk.nussknacker.prinz.util.repository.RepositoryClientConfig

import java.net.URI

class H2OConfig(private implicit val config: Config) extends RepositoryClientConfig {

  private implicit val BASE_CONFIG_PATH: String = "h2o."

  val fileExtension: String = getConfigValue("fileExtension", getString)

  val modelsDirectory: URI = getConfigValue("modelsDirectory", getUri)

  val modelDirectoryHrefSelector: Option[String] = getOptionConfigValue("modelDirectoryHrefSelector", getString)
}
