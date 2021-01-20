package pl.touk.nussknacker.prinz.mlflow.converter

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureName}
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}


class MLFDataTypeWrapper(val typing: TypingResult,
                         val dataValue: AnyRef)

object MLFDataTypeWrapper {

  implicit val encodeMLFDataType: Encoder[MLFDataTypeWrapper] = (data: MLFDataTypeWrapper) =>
    if (data.typing.canBeSubclassOf(Typed[Boolean])) {
      Json.fromBoolean(data.dataValue.asInstanceOf[Boolean])
    } else if (data.typing.canBeSubclassOf(Typed[Long])) {
      Json.fromLong(data.dataValue.asInstanceOf[Long])
    } else if (data.typing.canBeSubclassOf(Typed[Float])) {
      Json.fromFloatOrNull(data.dataValue.asInstanceOf[Float])
    } else if (data.typing.canBeSubclassOf(Typed[Double])) {
      Json.fromDoubleOrNull(data.dataValue.asInstanceOf[Double])
    } else if (data.typing.canBeSubclassOf(Typed[String])) {
      Json.fromString(data.dataValue.asInstanceOf[String])
    } else {
      throw new IllegalArgumentException("Unknown mlflow data type wrapper type: " + data.typing)
    }

  def apply(signature: ModelSignature, columns: List[String], index: Int, value: AnyRef): MLFDataTypeWrapper =
    new MLFDataTypeWrapper(extractType(signature, columns, index), value)

  private def extractType(signature: ModelSignature, columns: List[String], index: Int): TypingResult = {
    val columnName = SignatureName(columns(index))
    signature.getInputValueType(columnName).get.typingResult
  }
}
