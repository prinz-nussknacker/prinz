package pl.touk.nussknacker.prinz.util.repository.client

import pl.touk.nussknacker.prinz.util.config.BaseConfig
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getOptionConfigValue, getString, getUri}

import java.net.URI

trait RepositoryClientConfig extends BaseConfig {

  val fileExtension: String  = getConfigValue("fileExtension", getString)

  val modelsDirectory: URI = getConfigValue("modelsDirectory", getUri)

  val modelDirectoryHrefSelector: Option[String] = getOptionConfigValue("modelDirectoryHrefSelector", getString)
}
