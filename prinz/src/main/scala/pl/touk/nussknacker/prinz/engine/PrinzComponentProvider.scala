package pl.touk.nussknacker.prinz.engine

import com.typesafe.config.Config
import pl.touk.nussknacker.engine.api.component.{ComponentDefinition, ComponentProvider, NussknackerVersion}
import pl.touk.nussknacker.engine.api.process.ProcessObjectDependencies
import pl.touk.nussknacker.prinz.model.repository.ModelRepository

abstract class PrinzComponentProvider extends ComponentProvider {

  def getComponentRepository(config: Config, dependencies: ProcessObjectDependencies): ModelRepository

  override final def create(config: Config, dependencies: ProcessObjectDependencies): List[ComponentDefinition] = {
    val repository = getComponentRepository(config, dependencies)
    repository.listModels match {
      case Right(models) => models.map(model =>
        ComponentDefinition(model.toString, PrinzEnricher(model))
      )
      case Left(exc) => throw exc
    }
  }

  override def isCompatible(version: NussknackerVersion): Boolean = true

  override def resolveConfigForExecution(config: Config): Config = config
}
