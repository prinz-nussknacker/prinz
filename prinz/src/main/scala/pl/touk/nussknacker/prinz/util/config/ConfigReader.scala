package pl.touk.nussknacker.prinz.util.config

import java.net.URL

import com.typesafe.config.Config
import com.typesafe.scalalogging.Logger

object ConfigReader {

  private val logger = Logger[this.type]

  def getConfigValue[T](path: String, default: T, extractor: (Config, String) => T)(implicit config: Config, basePath: String): T = {
    val fullPath = s"$basePath$path"
    logger.info(s"Config used is ${config.toString}")
    if (config.hasPath(fullPath)) {
      val extracted = extractor(config, fullPath)
      logger.info("Config value {} defined with value {}", fullPath, extracted)
      extracted
    }
    else {
      logger.info("No config value {} defined. Using default value {}", fullPath, default)
      default
    }
  }

  def getUrl(config: Config, path: String): URL = new URL(config.getString(path))

  def getInt(config: Config, path: String): Int = config.getInt(path)

  def getString(config: Config, path: String): String = config.getString(path)

  def url(url: String): URL = new URL(url)
}
