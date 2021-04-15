package pl.touk.nussknacker.prinz.util.config

import java.net.URL
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

object ConfigReader extends LazyLogging {

  def getConfigValue[T](path: String, extractor: (Config, String) => T)(implicit config: Config, basePath: String): T =
    getOptionConfigValue(path, extractor) match {
      case Some(value) => value
      case None => throw new IllegalStateException(s"No config value defined for $basePath$path")
    }

  def getOptionConfigValue[T](path: String, extractor: (Config, String) => T)(implicit config: Config, basePath: String): Option[T] = {
    val fullPath = s"$basePath$path"
    if (config.hasPath(fullPath)) {
      val extracted = extractor(config, fullPath)
      logger.info("Config value {} defined with value {}", fullPath, extracted)
      Some(extracted)
    }
    else {
      logger.info("Config value {} not defined", fullPath)
      None
    }
  }

  def getUrl(config: Config, path: String): URL = new URL(config.getString(path))

  def getInt(config: Config, path: String): Int = config.getInt(path)

  def getString(config: Config, path: String): String = config.getString(path)
}
