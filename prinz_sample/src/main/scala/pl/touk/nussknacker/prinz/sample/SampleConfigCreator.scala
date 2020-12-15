package pl.touk.nussknacker.prinz.sample

import java.net.URL

import pl.touk.nussknacker.engine.api.Service
import pl.touk.nussknacker.engine.api.process.{ProcessObjectDependencies, SinkFactory, SourceFactory, WithCategories}
import pl.touk.nussknacker.engine.flink.util.sink.EmptySink
import pl.touk.nussknacker.engine.flink.util.transformer.PeriodicSourceFactory
import pl.touk.nussknacker.engine.util.process.EmptyProcessConfigCreator
import pl.touk.nussknacker.prinz.enrichers.PrinzEnricher
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepository

class SampleConfigCreator extends EmptyProcessConfigCreator {

  protected def allCategories[T](obj: T): WithCategories[T] = WithCategories(obj, "FraudDetection", "Recommendations")

  override def sourceFactories(processObjectDependencies: ProcessObjectDependencies): Map[String, WithCategories[SourceFactory[_]]] = Map(
    "periodic" -> allCategories(PeriodicSourceFactory)
  )

  override def sinkFactories(processObjectDependencies: ProcessObjectDependencies): Map[String, WithCategories[SinkFactory]] = Map(
    "empty" -> allCategories(SinkFactory.noParam(EmptySink))
  )

  override def services(processObjectDependencies: ProcessObjectDependencies): Map[String, WithCategories[Service]] = {
    val repo = MLFRepository(new URL("http://localhost:5000"))
    val response = repo.listModels

    if(response.isRight) {
      response.right.get.map(m => (m.getName.toString, allCategories(new PrinzEnricher))).toMap
    }
    else {
      Map()
    }
  }
}
