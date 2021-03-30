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
    } yield (mlfModels ++ pmmlModels).foldLeft(Map.empty[String, WithCategories[Service]])(
      (services, model) => services + (model.getName.toString -> allCategories(PrinzEnricher(model)))
    )

    result match {
      case Right(services) => services
      case Left(exception) => throw exception
    }
  }

  override def exceptionHandlerFactory(processObjectDependencies: ProcessObjectDependencies): ExceptionHandlerFactory =
    ExceptionHandlerFactory.noParams(VerboselyLoggingExceptionHandler(_))
}
