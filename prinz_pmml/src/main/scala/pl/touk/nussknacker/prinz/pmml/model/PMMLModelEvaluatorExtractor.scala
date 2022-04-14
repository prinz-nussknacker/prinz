package pl.touk.nussknacker.prinz.pmml.model

import org.jpmml.evaluator.{Evaluator, LoadingModelEvaluatorBuilder}
import org.jpmml.model.PMMLException
import pl.touk.nussknacker.prinz.model.ModelNotValidException
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelPayload
import pl.touk.nussknacker.prinz.util.resource.ResourceManager

object PMMLModelEvaluatorExtractor {

  def extractModelName(payload: PMMLModelPayload): Option[String] =
    ResourceManager.withOpened(payload.inputStreamSource()) { inputStream =>
      val evaluatorBuilder = new LoadingModelEvaluatorBuilder().load(inputStream)
      Option(evaluatorBuilder.getModel.getModelName)
    }

  def extractModelEvaluator(payload: PMMLModelPayload): Evaluator =
    ResourceManager.withOpened(payload.inputStreamSource()) { inputStream =>
      val evaluatorBuilder = new LoadingModelEvaluatorBuilder().load(inputStream)
      val evaluator = evaluatorBuilder.build()
      try {
        evaluator.verify()
      } catch {
        case ex: PMMLException => throw ModelNotValidException(ex)
      }
      evaluator
    }
}
