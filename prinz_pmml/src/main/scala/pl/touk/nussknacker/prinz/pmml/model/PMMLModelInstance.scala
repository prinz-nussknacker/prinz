package pl.touk.nussknacker.prinz.pmml.model

import com.typesafe.scalalogging.LazyLogging
import org.jpmml.evaluator.{Evaluator, EvaluatorUtil, PMMLException}
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap.VectorMultimapBuilder
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimapUtils.VectorMultimapAsRowset

import scala.concurrent.Future
import scala.jdk.CollectionConverters.{mapAsJavaMapConverter, mapAsScalaMapConverter}

case class PMMLModelInstance(private val evaluator: Evaluator,
                             override val model: PMMLModel)
  extends ModelInstance(model)
    with LazyLogging {

  override def runVerified(inputMap: ModelInputData): ModelRunResult = Future {
    try {
      val resultSeq = inputMap.mapRows(evaluateRow)
      val results = collectOutputs(resultSeq).asJava
      Right(results)
    } catch {
      case ex: PMMLException =>
        Left(new ModelRunException(ex))
    }
  }

  def evaluateRow(row: Map[String, AnyRef]): Map[String, _] = {
    val args = EvaluatorUtil.encodeKeys(row.asJava)
    val results = evaluator.evaluate(args)
    val decodeResult = EvaluatorUtil.decodeAll(results).asScala.toMap
    decodeResult
  }

  def collectOutputs(rows: IndexedSeq[Map[String, _]]): Map[String, _] = {
    rows.take(2).size match {
      case 0 => Map[String, Any]()
      case 1 => rows.head
      case 2 =>
        val builder = new VectorMultimapBuilder[String, Any]
        rows.foreach(r => builder ++= r)
        builder.result().toMap
    }
  }
}
