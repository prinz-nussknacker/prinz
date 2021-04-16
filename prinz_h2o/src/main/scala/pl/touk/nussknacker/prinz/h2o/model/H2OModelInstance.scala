package pl.touk.nussknacker.prinz.h2o.model

import com.typesafe.scalalogging.LazyLogging
import hex.genmodel.easy.{EasyPredictModelWrapper, RowData}
import hex.genmodel.easy.exception.PredictException
import hex.genmodel.easy.prediction.AbstractPrediction
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimapUtils.VectorMultimapAsRowset

import scala.concurrent.Future
import scala.jdk.CollectionConverters.{mapAsJavaMapConverter}

case class H2OModelInstance(private val modelWrapper: EasyPredictModelWrapper,
                            override val model: H2OModel)
  extends ModelInstance(model, H2OSignatureProvider)
    with LazyLogging {

  override def run(inputMap: ModelInputData): ModelRunResult = Future {
    try {
      val resultSeq = inputMap.mapRows(evaluateRow)
      logger.info("Mapped rows: {}", resultSeq)
      Right(Map.empty[String, Any].asJava)
    } catch {
      case ex: PredictException =>
        logger.warn("Got PredictException:", ex)
        Left(new ModelRunException(ex.toString))
    }
  }

  def evaluateRow(row: Map[String, AnyRef]): AbstractPrediction = {
    logger.info("Evaluate row {}", row)
    var rowData = new RowData()
    rowData.putAll(row.asJava)
    logger.info("Args for H2O evaluator: {}", rowData)
    val result = modelWrapper.predict(rowData)
    logger.info("Evaluation result: {}", result)
    // TODO: Extract result from AbstractPrediction based on modelWrapper.getModel().getModelCategory
    // See https://github.com/h2oai/h2o-3/blob/master/h2o-genmodel/src/main/java/hex/genmodel/easy/EasyPredictModelWrapper.java#L354
    result
  }
}
