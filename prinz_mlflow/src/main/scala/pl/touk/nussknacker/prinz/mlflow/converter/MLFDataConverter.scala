package pl.touk.nussknacker.prinz.mlflow.converter

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.Dataframe
import pl.touk.nussknacker.prinz.model.ModelInstance.ModelInputData
import pl.touk.nussknacker.prinz.model.ModelSignature
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimapUtils.VectorMultimapAsRowset

object MLFDataConverter extends LazyLogging {

  def outputToResultMap(output: MLFOutputDataTypeWrapper, signature: ModelSignature): Map[String, _] = {
    val outNames = signature.getSignatureOutputs.map(_.signatureName.name)
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
      val columns = signature.getSignatureInputs.map(_.signatureName.name)
      val data = input.mapRows(colToVal => {
        val wrapped = colToVal
          .transform(MLFInputDataTypeWrapper(signature, _, _))
        columns.map(wrapped(_))
      }).toList
      Dataframe(columns, data)
    }

  private def isMultimapConvertible(multimap: VectorMultimap[String, AnyRef]): Boolean =
    multimap.values.map(_.size).toSet.size <= 1
}
