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

  def inputToDataframe(input: VectorMultimap[String, AnyRef], signature: ModelSignature): Dataframe =
    if (!isMultimapConvertible(input)) {
      throw new IllegalArgumentException("Invalid multimap data given for mlflow data conversion")
    }
    else if (input.isEmpty) {
      Dataframe()
    }
    else {
      val columns = input.keys.toList
      val numberOfDataSeries = input.values.map(_.size).head
      val data = (0 until numberOfDataSeries).map(seriesIndex =>
        columns.map(columnName => {
          val value = input.get(columnName).get(seriesIndex)
          MLFInputDataTypeWrapper(signature, columnName, value)
        })
      ).toList

      Dataframe(columns, data)
    }

  private def isMultimapConvertible(multimap: VectorMultimap[String, AnyRef]): Boolean =
    multimap.values.map(_.size).toSet.size <= 1
}
