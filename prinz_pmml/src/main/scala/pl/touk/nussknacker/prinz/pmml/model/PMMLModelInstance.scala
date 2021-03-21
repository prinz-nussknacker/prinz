package pl.touk.nussknacker.prinz.pmml.model

import org.dmg.pmml.FieldName
import org.jpmml.evaluator.{Evaluator, EvaluatorUtil, FieldValue, PMMLException}
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException}
import pl.touk.nussknacker.prinz.pmml.model.VectorMultimapUtils.VectorMultimapAsRowset
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap.VectorMultimapBuilder

import java.util.{Map => JMap}
import scala.jdk.CollectionConverters.{mapAsJavaMapConverter, mapAsScalaMapConverter}
import scala.concurrent.Future

case class PMMLModelInstance(evaluator: Evaluator, override val model: PMMLModel)
  extends ModelInstance(model, PMMLSignatureProvider) {

  type PMMLArgs = JMap[FieldName, FieldValue]

  override def run(inputMap: ModelInputData): ModelRunResult = Future {
    try {
      val resultSeq = inputMap.forEachRow(evaluateRow)
      val results = collectOutputs(resultSeq).asJava
      Right(results)
    } catch {
      case ex: PMMLException => {
        Left(new ModelRunException(ex.toString))
      }
    }
  }

  def evaluateRow(row: Map[String, AnyRef]): Map[String, _] = {
    val args = EvaluatorUtil.encodeKeys(row.asJava)
    val results = evaluator.evaluate(args)
    EvaluatorUtil.decodeAll(results).asScala.toMap
  }

  def collectOutputs(rows: IndexedSeq[Map[String, _]]): Map[String, _] = {
    rows.take(2).size match {
      case 0 => Map[String, Any]()
      case 1 => rows.head
      case 2 => {
        val builder = new VectorMultimapBuilder[String, Any]
        rows.foreach(r => builder ++= r)
        builder.result().toMap
      }
    }
  }
}
