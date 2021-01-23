package pl.touk.nussknacker.prinz.mlflow.converter

import io.circe.Decoder.Result
import io.circe.{Decoder, DecodingFailure, HCursor}
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.model.ModelSignature

case class MLFOutputDataTypeWrapper(outputData: List[_])

object MLFOutputDataTypeWrapper {

  def getDecoderForSignature(signature: ModelSignature): Decoder[MLFOutputDataTypeWrapper] = new Decoder[MLFOutputDataTypeWrapper] {

    override def apply(c: HCursor): Result[MLFOutputDataTypeWrapper] = {
      val indexedJsonArrayOption = c.values
        .map(_.zipWithIndex)
      val result = indexedJsonArrayOption
        .map(indexedJsonArray => indexedJsonArray.map { case (json, idx) => json.as(getDecoderForIndex(idx)) })
        .flatMap(matchAllResult)
      result match {
        case Some(value) => Right(MLFOutputDataTypeWrapper(value))
        case None => Left(DecodingFailure("Error in decoding array output json", List.empty))
      }
    }

    private def getDecoderForIndex(index: Int): Decoder[_] = {
      val outputName = signature.getOutputNames(index)
      val outputType = signature.getOutputValueType(outputName).get.typingResult
      getDecoderForType(outputType)
    }

    private def getDecoderForType(typing: TypingResult): Decoder[_] = typing match {
      case t: TypingResult if t.canBeSubclassOf(Typed[Boolean]) => Decoder.decodeBoolean
      case t: TypingResult if t.canBeSubclassOf(Typed[Long]) => Decoder.decodeLong
      case t: TypingResult if t.canBeSubclassOf(Typed[Double]) => Decoder.decodeDouble
      case t: TypingResult if t.canBeSubclassOf(Typed[Float]) => Decoder.decodeFloat
      case t: TypingResult if t.canBeSubclassOf(Typed[String]) => Decoder.decodeString
      case _ => throw new IllegalArgumentException(s"Unknown mlflow data type wrapper type: $typing")
    }

    private def matchAllResult[A](l:  Iterable[Result[A]]): Option[List[A]] = l.foldRight(Option(List.empty[A])) {
      case (Right(value), Some(list)) => Option(value :: list)
      case (_, _) => None
    }
  }
}
