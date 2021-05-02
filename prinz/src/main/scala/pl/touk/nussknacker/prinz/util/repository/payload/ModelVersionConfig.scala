package pl.touk.nussknacker.prinz.util.repository.payload

import pl.touk.nussknacker.prinz.util.config.BaseConfig
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getString}

trait ModelVersionConfig extends BaseConfig {

  val modelVersionSeparator: String = getConfigValue("modelVersionSeparator", getString)
}
