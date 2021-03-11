package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.Evaluator
import pl.touk.nussknacker.prinz.model.ModelInstance
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

case class PMMLModelInstance(evaluator: Evaluator, override val model: PMMLModel)
  extends ModelInstance(model, PMMLSignatureProvider) {

  override def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult = ???
}