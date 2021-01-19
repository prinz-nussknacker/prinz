package pl.touk.nussknacker.prinz.util.nussknacker

import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}

object NKConverter {
  def toTypingResult(typeName: String): TypingResult = typeName match {
    case "boolean" => Typed[Boolean]
    case "integer" => Typed[Int]
    case "long" => Typed[Long]
    case "float" => Typed[Float]
    case "double" => Typed[Double]
    case "string" => Typed[String]
    case "binary" => Typed[Array[Byte]]
  }
}
