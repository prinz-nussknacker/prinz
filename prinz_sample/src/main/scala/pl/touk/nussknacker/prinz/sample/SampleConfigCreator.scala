package pl.touk.nussknacker.prinz.sample

import com.typesafe.config.Config
import pl.touk.nussknacker.engine.api.Service
import pl.touk.nussknacker.engine.api.exception.ExceptionHandlerFactory
import pl.touk.nussknacker.engine.api.process.{ProcessObjectDependencies, SinkFactory, SourceFactory, WithCategories}
import pl.touk.nussknacker.engine.flink.util.exception.{BrieflyLoggingExceptionHandler, VerboselyLoggingExceptionHandler}
import pl.touk.nussknacker.engine.flink.util.sink.EmptySink
import pl.touk.nussknacker.engine.flink.util.transformer.PeriodicSourceFactory
import pl.touk.nussknacker.engine.util.process.EmptyProcessConfigCreator
import pl.touk.nussknacker.prinz.enrichers.PrinzEnricher
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.api.LocalMLFModelLocationStrategy
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository
import pl.touk.nussknacker.prinz.model.Model
import pl.touk.nussknacker.prinz.pmml.PMMLConfig
import pl.touk.nussknacker.prinz.pmml.repository.HttpPMMLModelRepository

class SampleConfigCreator extends EmptyProcessConfigCreator {

  protected def allCategories[T](obj: T): WithCategories[T] = WithCategories(obj, "FraudDetection", "Recommendations")

  override def sourceFactories(processObjectDependencies: ProcessObjectDependencies): Map[String, WithCategories[SourceFactory[_]]] = Map(
    "periodic" -> allCategories(PeriodicSourceFactory),
    "periodicGaussianDouble" -> allCategories(PeriodicRandomGaussianDoubleSourceFactory)
  )

  override def sinkFactories(processObjectDependencies: ProcessObjectDependencies): Map[String, WithCategories[SinkFactory]] = Map(
    "empty" -> allCategories(SinkFactory.noParam(EmptySink)),
    "fraudDetected" -> allCategories(SinkFactory.noParam(LoggingSink("FRAUD DETECTED"))),
    "fraudNotDetected" -> allCategories(SinkFactory.noParam(LoggingSink("FRAUD NOT DETECTED")))
  )

  override def services(processObjectDependencies: ProcessObjectDependencies): Map[String, WithCategories[Service]] = {
    implicit val config: Config = processObjectDependencies.config

    val modelLocationStrategy = LocalMLFModelLocationStrategy
    implicit val mlfConfig: MLFConfig = MLFConfig(modelLocationStrategy)

    implicit val pmmlConfig: PMMLConfig = PMMLConfig()

    val mlfRepository = new MLFModelRepository()
    val mlfModelsResult = mlfRepository.listModels

    val pmmlRepository = new HttpPMMLModelRepository()
    val pmmlModelsResult = pmmlRepository.listModels

    val result = for {
      mlfModels <- mlfModelsResult
      pmmlModels <- pmmlModelsResult
    } yield (addRepositoryName(mlfModels, "mlf") ++ addRepositoryName(pmmlModels, "pmml"))
      .foldLeft(Map.empty[String, WithCategories[Service]]) {
        (services, modelRepository) => services + createModelEnricherRepresentation(modelRepository)
    }

    result match {
      case Right(services) => services
      case Left(exception) => throw exception
    }
  }

  override def exceptionHandlerFactory(processObjectDependencies: ProcessObjectDependencies): ExceptionHandlerFactory =
    ExceptionHandlerFactory.noParams(VerboselyLoggingExceptionHandler(_))

  private def addRepositoryName(models: List[Model], repository: String): List[(Model, String)] =
    models.map(model => (model, repository))

  private def createModelEnricherRepresentation(modelRepository: (Model, String)): (String, WithCategories[Service]) =
    createModelNameWithRepository(modelRepository) -> allCategories(createEnricher(modelRepository))

  private def createModelNameWithRepository(modelRepository: (Model, String)): String =
    s"${modelRepository._2}-${modelRepository._1.getName.toString}"

  private def createEnricher(modelRepository: (Model, String)): PrinzEnricher = PrinzEnricher(modelRepository._1)
}
