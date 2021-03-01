package pl.touk.nussknacker.prinz.pmml

import com.typesafe.config.Config
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getString, getUrl, url}

import java.net.URL

final case class PMMLConfig(private implicit val config: Config) {

  private implicit val BASE_CONFIG_PATH: String = "pmml."

  val modelsDirectory: URL = getConfigValue("modelsDirectory", url("http://localhost:5100/"), getUrl)

  val modelDirectoryHrefSelector: String = getConfigValue("modelDirectoryHrefSelector", "body > ul > li > a", getString)
}
