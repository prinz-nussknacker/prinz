package pl.touk.nussknacker.prinz.h2o

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import hex.genmodel.MojoReaderBackendFactory.CachingStrategy
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getString}
import pl.touk.nussknacker.prinz.util.repository.client.RepositoryClientConfig
import pl.touk.nussknacker.prinz.util.repository.payload.ModelVersionConfig

final case class H2OConfig(protected implicit val config: Config)
  extends RepositoryClientConfig
    with ModelVersionConfig
    with LazyLogging {

  override protected implicit def baseConfigPath: String = "h2o."

  val cachingStrategy: CachingStrategy  = getCachingStrategyForName(getConfigValue("cachingStrategy", getString))

  private val DEFAULT_CACHING_STRATEGY: CachingStrategy = CachingStrategy.MEMORY

  private def getCachingStrategyForName(name: String): CachingStrategy = try {
    CachingStrategy.valueOf(name)
  } catch {
    case _: IllegalArgumentException =>
      logger.warn(s"Using default H2O model cache strategy as got unknown name $name")
      DEFAULT_CACHING_STRATEGY
  }
}
