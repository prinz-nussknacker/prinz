package pl.touk.nussknacker.prinz.pmml.model

import org.dmg.pmml.FieldName
import org.jpmml.evaluator.{Evaluator, EvaluatorUtil, FieldValue, InputField, PMMLException}
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException}
import pl.touk.nussknacker.prinz.pmml.utils.VectorMultimapAsRowset
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import java.util.{Map => JMap}
import scala.jdk.CollectionConverters._
import scala.concurrent.Future

case class PMMLModelInstance(evaluator: Evaluator, override val model: PMMLModel)
  extends ModelInstance(model, PMMLSignatureProvider) {

  type PMMLArgs = JMap[FieldName, FieldValue]

  override def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult = Future {
    try {
      val results = inputMap.forEachRow(evaluateRow)
      Right(collectOutputs(results).asJava)
    } catch {
      case ex: PMMLException => {
        Left(new ModelRunException(ex.toString))
      }
    }
  }

  def evaluateRow(row: Map[String, AnyRef]): Map[String, _] = {
    val args = this.buildArguments(row);
    val results = evaluator.evaluate(args)
    EvaluatorUtil.decodeAll(results).asScala.toMap
    // TODO(kantoniak): Check if we have to handle complex PMML types
  }

  def buildArguments(row: Map[String, AnyRef]): PMMLArgs = {
    val inputNameToField = evaluator.getInputFields.asScala.map((field: InputField) => (field.getName.getValue, field)).toMap
    (row map {
      case (key, value) => (
          FieldName.create(key),
          inputNameToField(key).prepare(value)
      )
    } toMap).asJava
  }

  def collectOutputs(rows: IndexedSeq[Map[String, _]]): Map[String, _] = {
    rows.head
    // TODO(kantoniak): Collect and merge inputs
  }
}
