package pl.touk.nussknacker.prinz.h2o.model

import com.typesafe.scalalogging.LazyLogging
import hex.ModelCategory
import hex.genmodel.easy.{EasyPredictModelWrapper, RowData}
import hex.genmodel.easy.exception.PredictException
import hex.genmodel.easy.prediction.{
  AbstractPrediction, AnomalyDetectionPrediction, BinomialModelPrediction,
  ClusteringModelPrediction, CoxPHModelPrediction, KLimeModelPrediction,
  MultinomialModelPrediction, OrdinalModelPrediction, RegressionModelPrediction}
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.h2o.converter.H2ODataConverter
import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimapUtils.VectorMultimapAsRowset

import scala.concurrent.Future
import scala.jdk.CollectionConverters.mapAsJavaMapConverter

case class H2OModelInstance(private val modelWrapper: EasyPredictModelWrapper,
                            override val model: H2OModel)
  extends ModelInstance(model) {

  override protected def runVerified(inputMap: ModelInputData): ModelRunResult = Future {
    try {
      val convertedInputMap = H2ODataConverter.inputToTypedModelInput(inputMap, model.getMetadata.signature)
      val resultSeq = convertedInputMap.mapRows(evaluateRow)
      val results = collectOutputs(resultSeq).asJava
      Right(results)
    } catch {
      case ex: PredictException =>
        Left(new ModelRunException(ex))
    }
  }

  private def evaluateRow(row: Map[String, Any]): AbstractPrediction = {
    val rowData = new RowData()
    rowData.putAll(row.map{case (key, value) => key -> value.asInstanceOf[AnyRef]}.asJava)
    val result = modelWrapper.predict(rowData)
    result
  }

  private def getTransformer(modelCategory: ModelCategory)(p: AbstractPrediction): Any = modelCategory match {
    case ModelCategory.AnomalyDetection => p.asInstanceOf[AnomalyDetectionPrediction].isAnomaly
    case ModelCategory.Binomial         => p.asInstanceOf[BinomialModelPrediction].labelIndex
    case ModelCategory.Multinomial      => p.asInstanceOf[MultinomialModelPrediction].labelIndex
    case ModelCategory.Ordinal          => p.asInstanceOf[OrdinalModelPrediction].labelIndex
    case ModelCategory.Clustering       => p.asInstanceOf[ClusteringModelPrediction].cluster
    case ModelCategory.KLime            => p.asInstanceOf[KLimeModelPrediction].cluster
    case ModelCategory.CoxPH            => p.asInstanceOf[CoxPHModelPrediction].value
    case ModelCategory.Regression       => p.asInstanceOf[RegressionModelPrediction].value
    case _ => throw new ModelRunException(s"ModelCategory $modelCategory not supported.")
  }

  private def collectOutputs(rows: IndexedSeq[AbstractPrediction]): Map[String, _] = {
    rows.take(1).size match {
      case 0 => Map[String, Any]()
      case 1 =>
        val returnFieldDef = model.getMetadata.signature.getSignatureOutputs.head
        val transformer = getTransformer(modelWrapper.m.getModelCategory)(_)
        Map(returnFieldDef.signatureName.name -> rows.map(transformer))
    }
  }
}
