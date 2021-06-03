package pl.touk.nussknacker.prinz.pmml

import com.typesafe.config.Config
import pl.touk.nussknacker.engine.api.process.ProcessObjectDependencies
import pl.touk.nussknacker.prinz.engine.PrinzComponentProvider
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository

class PMMLComponentProvider extends PrinzComponentProvider {

  override def providerName: String = "prinzPMML"

  override final def getComponentRepository(config: Config, dependencies: ProcessObjectDependencies): ModelRepository = {
    implicit val implicitConfig: Config = config
    implicit val pmmlConfig: PMMLConfig = PMMLConfig()
    new PMMLModelRepository()
  }
}
