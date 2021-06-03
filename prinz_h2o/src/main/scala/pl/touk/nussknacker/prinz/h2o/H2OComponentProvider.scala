package pl.touk.nussknacker.prinz.h2o

import com.typesafe.config.Config
import pl.touk.nussknacker.engine.api.process.ProcessObjectDependencies
import pl.touk.nussknacker.prinz.engine.PrinzComponentProvider
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelRepository
import pl.touk.nussknacker.prinz.model.repository.ModelRepository

class H2OComponentProvider extends PrinzComponentProvider {

  override def providerName: String = "prinzH2O"

  override final def getComponentRepository(config: Config, dependencies: ProcessObjectDependencies): ModelRepository = {
    implicit val config: Config = dependencies.config
    implicit val h2oConfig: H2OConfig = H2OConfig()
    new H2OModelRepository()
  }
}
