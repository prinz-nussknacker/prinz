package pl.touk.nussknacker.prinz.h2o

import com.typesafe.config.Config
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getOptionConfigValue, getString, getUri}

import java.net.URI

class H2OConfig(private implicit val config: Config) {

  private implicit val BASE_CONFIG_PATH: String = "h2o."

  val modelsDirectory: URI = getConfigValue("modelsDirectory", getUri)

}
