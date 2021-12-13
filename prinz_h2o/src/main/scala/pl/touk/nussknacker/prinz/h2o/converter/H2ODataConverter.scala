package pl.touk.nussknacker.prinz.h2o.converter

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.model.ModelInstance.ModelInputData
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureName, SignatureType}

object H2ODataConverter extends LazyLogging {

  private val STRING_WRAPPER: String = "'"

  def inputToTypedModelInput(input: ModelInputData, signature: ModelSignature): ModelInputData =
    input.mapValuesWithKeys(wrappedByDefinitionFrom(signature))

  private def wrappedByDefinitionFrom(signature: ModelSignature)(key: String, value: Any): Any =
    signature.getInputValueType(SignatureName(key)) match {
      case Some(signatureType) => mapValueBySignature(value, signatureType)
      case None => throw new IllegalStateException(s"Found data column not defined in signature with name: $key")
    }

  private def mapValueBySignature(value: Any, signatureType: SignatureType): Any =
    signatureType.typingResult match {
      case t: TypingResult if t.canBeSubclassOf(Typed[String]) => wrapStringInput(value)
      case t: TypingResult if t.canBeSubclassOf(Typed[Double]) => value
      case t: TypingResult => throw new IllegalStateException(s"Found not expected type in signature of H2O model: $t")
    }

  private def wrapStringInput(input: Any): Any = {
    input match {
      case stringValue: String =>
        val beginFixed = if (stringValue.startsWith(STRING_WRAPPER)) stringValue else STRING_WRAPPER + stringValue
        val endFixed = if (beginFixed.endsWith(STRING_WRAPPER)) beginFixed else beginFixed + STRING_WRAPPER
        endFixed
      case _ => throw new IllegalStateException("Expected String value on field encoded as String typed input")
    }
  }
}
