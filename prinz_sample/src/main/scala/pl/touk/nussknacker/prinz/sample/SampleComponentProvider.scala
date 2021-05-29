package pl.touk.nussknacker.prinz.sample

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.engine.api.component.{ComponentDefinition, ComponentProvider, NussknackerVersion}
import pl.touk.nussknacker.engine.api.process.ProcessObjectDependencies
import pl.touk.nussknacker.prinz.enrichers.PrinzEnricher
import pl.touk.nussknacker.prinz.h2o.H2OConfig
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelRepository
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.api.LocalMLFModelLocationStrategy
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository
import pl.touk.nussknacker.prinz.model.repository.{CompositeModelRepository, ModelRepository}
import pl.touk.nussknacker.prinz.pmml.PMMLConfig
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository

class SampleComponentProvider extends ComponentProvider with LazyLogging {

  override def providerName: String = "Prinz Models Sample Component Provider"

  override def resolveConfigForExecution(config: Config): Config = config

  override def create(config: Config, dependencies: ProcessObjectDependencies): List[ComponentDefinition] = {
    implicit val config: Config = dependencies.config

    val modelLocationStrategy = LocalMLFModelLocationStrategy
    implicit val mlfConfig: MLFConfig = MLFConfig(modelLocationStrategy)
    implicit val pmmlConfig: PMMLConfig = PMMLConfig()
    implicit val h2oConfig: H2OConfig = H2OConfig()

    val mlfRepository = new MLFModelRepository()
    val pmmlRepository = new PMMLModelRepository()
    val h2oRepository = new H2OModelRepository()

    val repository = CompositeModelRepository(
      mlfRepository,
      pmmlRepository,
      h2oRepository
    )
    val modelsResult = repository.listModels

    modelsResult match {
      case Right(models) => models.map(model =>
        ComponentDefinition(model.toString, PrinzEnricher(model))
      )
      case Left(exc) => throw exc
    }
  }

  override def isCompatible(version: NussknackerVersion): Boolean = true
}
