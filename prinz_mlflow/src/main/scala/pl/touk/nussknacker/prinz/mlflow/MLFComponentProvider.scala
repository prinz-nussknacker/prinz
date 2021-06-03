package pl.touk.nussknacker.prinz.mlflow

import com.typesafe.config.Config
import pl.touk.nussknacker.engine.api.process.ProcessObjectDependencies
import pl.touk.nussknacker.prinz.engine.PrinzComponentProvider
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository
import pl.touk.nussknacker.prinz.model.repository.ModelRepository

class MLFComponentProvider extends PrinzComponentProvider {

  override def providerName: String = "prinzMLflow"

  override final def getComponentRepository(config: Config, dependencies: ProcessObjectDependencies): ModelRepository = {
    implicit val implicitConfig: Config = config
    implicit val mlfConfig: MLFConfig = MLFConfig()
    new MLFModelRepository()
  }
}
