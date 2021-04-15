package pl.touk.nussknacker.prinz.pmml.converter

import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}

object PMMLSignatureInterpreter {

  // scalastyle:off
  def fromPMMLDataType(typeName: String): TypingResult = typeName match {
    case "string" => Typed[String]
    case "boolean" => Typed[Boolean]
    case "decimal" => Typed[BigDecimal]
    case "float" => Typed[Float]
    case "double" => Typed[Double]
    case "integer" => Typed[BigInt]
    case "nonPositiveInteger" => Typed[BigInt]
    case "negativeInteger" => Typed[BigInt]
    case "long" => Typed[Long]
    case "int" => Typed[Int]
    case "short" => Typed[Short]
    case "byte" => Typed[Byte]
    case "nonNegativeInteger" => Typed[BigInt]
    case "unsignedLong" => Typed[Long]
    case "unsignedInt" => Typed[Int]
    case "unsignedShort" => Typed[Short]
    case "unsignedByte" => Typed[Byte]
    case "positiveInteger" => Typed[BigInt]
    case _ => throw new IllegalArgumentException(s"Unknown PMMLDataType: $typeName")
    //TODO Potentially add support for other types from XMLSchema-2 like time and date
  }
  // scalastyle:on
}
