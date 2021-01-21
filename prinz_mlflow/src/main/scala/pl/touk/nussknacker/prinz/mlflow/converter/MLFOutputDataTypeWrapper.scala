package pl.touk.nussknacker.prinz.mlflow.converter

import io.circe.Decoder.Result
import io.circe.{Decoder, DecodingFailure, HCursor}
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.model.ModelSignature

case class MLFOutputDataTypeWrapper(outputData: List[_])

object MLFOutputDataTypeWrapper {

  def getDecoderForSignature(signature: ModelSignature): Decoder[MLFOutputDataTypeWrapper] = new Decoder[MLFOutputDataTypeWrapper] {

    override def apply(c: HCursor): Result[MLFOutputDataTypeWrapper] = {
      val result = c.values
        .map(_.zipWithIndex)
        .map(iterable => iterable.map { case (json, idx) => json.as(getDecoderForIndex(idx)) })
        .flatMap(matchAllResult)
      result match {
        case Some(value) => Right(MLFOutputDataTypeWrapper(value))
        case None => Left(DecodingFailure("Error in decoding array output json", List.empty))
      }
    }

    private def getDecoderForIndex(index: Int): Decoder[_] = {
      val output = signature.getOutputNames(index)
      getDecoderForType(signature.getOutputValueType(output).get.typingResult)
    }
  }

  private def matchAllResult[A](l:  Iterable[Result[A]]): Option[List[A]] = l.foldRight(Option(List.empty[A])) {
    case (Right(value), Some(list)) => Option(value :: list)
    case (_, _) => None
  }

  private def getDecoderForType[A](typing: TypingResult): Decoder[_] =
    if (typing.canBeSubclassOf(Typed[Boolean])) {
      Decoder.decodeBoolean
    } else if (typing.canBeSubclassOf(Typed[Long])) {
      Decoder.decodeLong
    } else if (typing.canBeSubclassOf(Typed[Double])) {
      Decoder.decodeDouble
    } else if (typing.canBeSubclassOf(Typed[Float])) {
      Decoder.decodeFloat
    } else if (typing.canBeSubclassOf(Typed[String])) {
      Decoder.decodeString
    } else {
      throw new IllegalArgumentException(s"Unknown mlflow data type wrapper type: $typing")
    }
}
