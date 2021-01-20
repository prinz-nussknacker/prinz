package pl.touk.nussknacker.prinz.mlflow.converter

import io.circe.{Encoder, Json}
import pl.touk.nussknacker.engine.api.typed.typing.TypingResult
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureName}

import scala.Proxy.Typed

class MLFDataTypeWrapper(val dataType: TypingResult,
                         val dataValue: AnyRef)

object MLFDataTypeWrapper {

  implicit val encodeMlfDataType: Encoder[MLFDataTypeWrapper] = (data: MLFDataTypeWrapper) => data.dataType match {
    case _: Typed[Boolean] => Json.fromBoolean(data.dataValue.asInstanceOf[Boolean])
    case _: Typed[Long] => Json.fromLong(data.dataValue.asInstanceOf[Long])
    case _: Typed[Float] => Json.fromFloatOrNull(data.dataValue.asInstanceOf[Float])
    case _: Typed[Double] => Json.fromDoubleOrNull(data.dataValue.asInstanceOf[Double])
    case _: Typed[String] => Json.fromString(data.dataValue.asInstanceOf[String])
    case _ => throw new IllegalArgumentException("Unknown mlflow data type wrapper type: " + data.dataType)
  }

  def apply(signature: ModelSignature, columns: List[String], index: Int, value: AnyRef): MLFDataTypeWrapper =
    new MLFDataTypeWrapper(extractType(signature, columns, index), value)

  private def extractType(signature: ModelSignature, columns: List[String], index: Int): TypingResult = {
    val columnName = SignatureName(columns(index))
    signature.getInputValueType(columnName).get.typingResult
  }
}
