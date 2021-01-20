package pl.touk.nussknacker.prinz.mlflow.converter

import com.typesafe.scalalogging.LazyLogging
import io.circe.Decoder
import io.circe.jawn.decode
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.Dataframe
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureProvider}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

object MLFDataConverter extends LazyLogging {

  def outputToResultMap(output: String, signature: ModelSignature): Map[String, AnyRef] = {
    // TODO take into account the signature of model when parsing output
    val decoder = Decoder.decodeList(Decoder.decodeDouble)
    val decoded = decode(output)(decoder)
    decoded.toOption.map { outputList =>
      val indexedPairs = outputList.zipWithIndex
        .map(toNamedByIndexAnyRefValue)
      indexedPairs.toMap
    }.get
  }

  def inputToDataframe(input: VectorMultimap[String, AnyRef], signature: ModelSignature): Dataframe = {
    if (!isMultimapConvertible(input)) {
      throw new IllegalArgumentException("Invalid multimap data given for mlflow data conversion")
    }

    val columns = input.keys.toList
    val data = input
      .values
      .flatMap(_.zipWithIndex)
      .map { case (v, idx) => (MLFDataTypeWrapper(signature, columns, idx, v), idx) }
      .groupBy { case (_, idx) => idx }
      .map { case (_, v) => v.map(_._1).toList }
      .toList

    Dataframe(columns, data)
  }

  private def toNamedByIndexAnyRefValue(valueIndex: (Double, Int)): (String, AnyRef) =
    (SignatureProvider.indexedOutputName(valueIndex._2).name, valueIndex._1.asInstanceOf[AnyRef])

  private def isDataframeConvertible(dataframe: Dataframe): Boolean =
    !dataframe.data.exists(record => record.length != dataframe.columns.length)

  private def isMultimapConvertible(multimap: VectorMultimap[String, AnyRef]): Boolean =
    multimap.values.map(_.size).toSet.size <= 1
}
