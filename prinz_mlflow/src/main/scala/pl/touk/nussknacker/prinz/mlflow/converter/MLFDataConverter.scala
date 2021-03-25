package pl.touk.nussknacker.prinz.mlflow.converter

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.Dataframe
import pl.touk.nussknacker.prinz.model.ModelInstance.ModelInputData
import pl.touk.nussknacker.prinz.model.ModelSignature
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimapUtils.VectorMultimapAsRowset

object MLFDataConverter extends LazyLogging {

  def outputToResultMap(output: MLFOutputDataTypeWrapper, signature: ModelSignature): Map[String, _] = {
    val outNames = signature.getOutputNames.map(_.name)
    outNames.zip(output.outputData)
      .toMap
  }

  def inputToDataframe(input: ModelInputData, signature: ModelSignature): Dataframe =
    if (!isMultimapConvertible(input)) {
      throw new IllegalArgumentException("Invalid multimap data given for mlflow data conversion")
    }
    else if (input.isEmpty) {
      Dataframe()
    }
    else {
      val columns = signature.signatureInputs.map(_.signatureName.name).toList
      val data = input.forEachRow(colToVal => {
        val wrapped = colToVal
          .filterKeys(columns.contains) // silently drop columns not present in signature
          .transform(MLFInputDataTypeWrapper(signature, _, _))
          .toMap
        columns.map(wrapped(_)).toList
      }).toList
      Dataframe(columns, data)
    }

  private def isMultimapConvertible(multimap: VectorMultimap[String, AnyRef]): Boolean =
    multimap.values.map(_.size).toSet.size <= 1
}
