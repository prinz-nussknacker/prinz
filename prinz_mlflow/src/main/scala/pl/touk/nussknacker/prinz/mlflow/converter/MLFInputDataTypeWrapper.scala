package pl.touk.nussknacker.prinz.mlflow.converter

import io.circe.{Encoder, Json}
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureName}
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}


case class MLFInputDataTypeWrapper private(typing: TypingResult, dataValue: AnyRef) {
  override def toString: String = s"MLFInputDataTypeWrapper($dataValue: ${typing.display})"
}

object MLFInputDataTypeWrapper {

  implicit val encodeMLFDataType: Encoder[MLFInputDataTypeWrapper] = (data: MLFInputDataTypeWrapper) =>
    data.typing match {
      case t: TypingResult if t.canBeSubclassOf(Typed[Boolean]) => Json.fromBoolean(data.dataValue.asInstanceOf[Boolean])
      case t: TypingResult if t.canBeSubclassOf(Typed[Long]) => Json.fromLong(data.dataValue.asInstanceOf[Long])
      case t: TypingResult if t.canBeSubclassOf(Typed[Double]) => Json.fromDoubleOrNull(data.dataValue.asInstanceOf[Double])
      case t: TypingResult if t.canBeSubclassOf(Typed[Float]) => Json.fromFloatOrNull(data.dataValue.asInstanceOf[Float])
      case t: TypingResult if t.canBeSubclassOf(Typed[String]) => Json.fromString(data.dataValue.asInstanceOf[String])
      case _ => throw new IllegalArgumentException(s"Unknown mlflow data type wrapper type: ${data.typing}")
    }

  def apply(signature: ModelSignature, columnName: String, value: AnyRef): MLFInputDataTypeWrapper = {
    val columnType = extractColumnType(signature, columnName)
    new MLFInputDataTypeWrapper(columnType, value)
  }

  private def extractColumnType(signature: ModelSignature, columnName: String): TypingResult = {
    val signatureColumnName = SignatureName(columnName)
    signature.getInputValueType(signatureColumnName).get.typingResult
  }
}
