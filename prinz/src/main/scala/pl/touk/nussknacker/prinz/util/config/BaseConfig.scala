package pl.touk.nussknacker.prinz.util.config

import com.typesafe.config.Config

trait BaseConfig {

  protected implicit val config: Config

  protected implicit def baseConfigPath: String
}
