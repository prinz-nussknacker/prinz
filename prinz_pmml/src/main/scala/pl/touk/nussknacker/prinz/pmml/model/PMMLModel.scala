package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.Evaluator
import org.jpmml.evaluator.LoadingModelEvaluatorBuilder
import java.io.File

class PMMLModel {
  //TODO we need evaluator in PMMLModel to get the signature
  //PMMLModelInstance should use this evaluator for scoring
  val evaluator: Evaluator = new LoadingModelEvaluatorBuilder().load(new File("")).build()
}
