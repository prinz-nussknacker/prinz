package pl.touk.nussknacker.prinz.mlflow.converter

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.Dataframe
import pl.touk.nussknacker.prinz.model.ModelSignature
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

object MLFDataConverter extends LazyLogging {

  def outputToResultMap(output: MLFOutputDataTypeWrapper, signature: ModelSignature): Map[String, _] = {
    val outNames = signature.getOutputNames.map(_.name)
    outNames.zip(output.outputData)
      .toMap
  }

  def inputToDataframe(input: VectorMultimap[String, AnyRef], signature: ModelSignature): Dataframe = {
    if (!isMultimapConvertible(input)) {
      throw new IllegalArgumentException("Invalid multimap data given for mlflow data conversion")
    }

    val columns = input.keys.toList
    val data = input
      .values
      .flatMap(_.zipWithIndex)
      .map { case (v, idx) => (MLFInputDataTypeWrapper(signature, columns, idx, v), idx) }
      .groupBy { case (_, idx) => idx }
      .map { case (_, v) => v.map(_._1).toList }
      .toList

    Dataframe(columns, data)
  }

  private def getSignaturePositionedValidator(signature: ModelSignature)(index: Int, elem: Either[String, Double]): AnyRef = {
//    val outputs = signature.getSignatureOutputs
//    if (index >= outputs.size) {
//      throw new IllegalArgumentException(s"Too many outputs in result - index $index not match signature: $signature")
//    }
//    val outputType = outputs(index).signatureType.typingResult
//    if (outputType.canBeSubclassOf(Typed[Boolean])) {
//      Json.fromBoolean(data.dataValue.asInstanceOf[Boolean])
//    } else if (outputType.canBeSubclassOf(Typed[Long])) {
//      Json.fromLong(data.dataValue.asInstanceOf[Long])
//    } else if (outputTypecanBeSubclassOf(Typed[Double])) {
//      Json.fromDoubleOrNull(data.dataValue.asInstanceOf[Double])
//    } else if (outputType.canBeSubclassOf(Typed[Float])) {
//      Json.fromFloatOrNull(data.dataValue.asInstanceOf[Float])
//    } else if (outputType.canBeSubclassOf(Typed[String])) {
//      Json.fromString(data.dataValue.asInstanceOf[String])
//    } else {
//      throw new IllegalArgumentException("Unknown mlflow data type wrapper type: " + data.typing)
//    }

    signature
  }

  private def isDataframeConvertible(dataframe: Dataframe): Boolean =
    !dataframe.data.exists(record => record.length != dataframe.columns.length)

  private def isMultimapConvertible(multimap: VectorMultimap[String, AnyRef]): Boolean =
    multimap.values.map(_.size).toSet.size <= 1
}
