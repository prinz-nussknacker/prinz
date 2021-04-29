package pl.touk.nussknacker.prinz.pmml

import com.typesafe.config.Config
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getOptionConfigValue, getString, getUri, getUrl}

import java.net.{URI, URL}

final case class PMMLConfig(private implicit val config: Config) {

  private implicit val BASE_CONFIG_PATH: String = "pmml."

  val modelsDirectory: URI = getConfigValue("modelsDirectory", getUri)

  val modelDirectoryHrefSelector: Option[String] = getOptionConfigValue("modelDirectoryHrefSelector", getString)
}
