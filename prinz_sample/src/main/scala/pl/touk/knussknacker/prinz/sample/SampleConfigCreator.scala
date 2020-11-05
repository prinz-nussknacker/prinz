package pl.touk.nussknacker.prinz.sample

import pl.touk.nussknacker.engine.api.process.WithCategories
import pl.touk.nussknacker.engine.util.process.EmptyProcessConfigCreator
import pl.touk.nussknacker.prinz.enrichers.IrisSpeciesEnricher

class SampleConfigCreator extends EmptyProcessConfigCreator {

  protected def defaultCategory[T](obj: T): WithCategories[T] = WithCategories(obj, "Default")

  override def services(config: Config): Map[String, WithCategories[Service]] = Map(
    "irisSpeciesEnricher" -> defaultCategory(new IrisSpeciesEnricher)
  )
}
