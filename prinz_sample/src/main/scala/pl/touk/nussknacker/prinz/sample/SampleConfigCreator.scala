package pl.touk.nussknacker.prinz.sample

import com.typesafe.config.Config
import pl.touk.nussknacker.engine.api.Service
import pl.touk.nussknacker.engine.api.exception.ExceptionHandlerFactory
import pl.touk.nussknacker.engine.api.process.{ProcessObjectDependencies, SinkFactory, SourceFactory, WithCategories}
import pl.touk.nussknacker.engine.flink.util.exception.VerboselyLoggingExceptionHandler
import pl.touk.nussknacker.engine.flink.util.sink.EmptySink
import pl.touk.nussknacker.engine.flink.util.transformer.PeriodicSourceFactory
import pl.touk.nussknacker.engine.util.process.EmptyProcessConfigCreator
import pl.touk.nussknacker.prinz.enrichers.PrinzEnricher
import pl.touk.nussknacker.prinz.h2o.H2OConfig
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelRepository
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.api.LocalMLFModelLocationStrategy
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository
import pl.touk.nussknacker.prinz.model.Model
import pl.touk.nussknacker.prinz.model.repository.CompositeModelRepository
import pl.touk.nussknacker.prinz.pmml.PMMLConfig
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository

class SampleConfigCreator extends EmptyProcessConfigCreator {

  protected def allCategories[T](obj: T): WithCategories[T] = WithCategories(obj, "FraudDetection", "Recommendations")

  override def sourceFactories(processObjectDependencies: ProcessObjectDependencies): Map[String, WithCategories[SourceFactory[_]]] = Map(
    "periodic" -> allCategories(PeriodicSourceFactory),
    "periodicGaussianDouble" -> allCategories(PeriodicRandomGaussianDoubleSourceFactory)
  )

  override def sinkFactories(processObjectDependencies: ProcessObjectDependencies): Map[String, WithCategories[SinkFactory]] = Map(
    "empty" -> allCategories(SinkFactory.noParam(EmptySink)),
    "logMessage" -> allCategories(SinkFactory.noParam(LoggingSink))
  )

  override def services(processObjectDependencies: ProcessObjectDependencies): Map[String, WithCategories[Service]] = {
    implicit val config: Config = processObjectDependencies.config

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

    val result = for {
      models <- modelsResult
    } yield models.foldLeft(Map.empty[String, WithCategories[Service]]) {
        (services, model) => services + createModelEnricherRepresentation(model)
    }

    result match {
      case Right(services) => services
      case Left(exception) => throw exception
    }
  }

  override def exceptionHandlerFactory(processObjectDependencies: ProcessObjectDependencies): ExceptionHandlerFactory =
    ExceptionHandlerFactory.noParams(VerboselyLoggingExceptionHandler(_))

  private def createModelEnricherRepresentation(model: Model): (String, WithCategories[Service]) =
    model.getMetadata.modelName.internal -> allCategories(PrinzEnricher(model))
}
