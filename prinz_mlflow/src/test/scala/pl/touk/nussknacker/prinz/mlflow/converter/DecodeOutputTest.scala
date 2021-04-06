package pl.touk.nussknacker.prinz.mlflow.converter

import io.circe.jawn.parse
import io.circe.syntax.EncoderOps
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureField, SignatureName, SignatureType}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

class DecodeOutputTest extends UnitTest {

  it should "decode output from JSON" in {
    val signature = createTestsSampleSignature(
      List(Typed[Double], Typed[String], Typed[Boolean]))
    val decoder = MLFOutputDataTypeWrapper.getDecoderForSignature(signature)
    val outputString = "[ 0.4234, \"output_string\", true ]"
    val expected = MLFOutputDataTypeWrapper(
      List(
        0.4234.asInstanceOf[AnyRef],
        "output_string".asInstanceOf[AnyRef],
        true.asInstanceOf[AnyRef],
      ))

    val outputJson = parse(outputString).toOption
    outputJson.isDefined should be (true)

    val decoded = decoder.decodeJson(outputJson.get).toOption

    decoded.isDefined should be (true)
    decoded.get should equal (expected)
  }

  it should "decode output with single value from JSON" in {
    val signature = createTestsSampleSignature(List(Typed[Double]))
    val decoder = MLFOutputDataTypeWrapper.getDecoderForSignature(signature)
    val outputString = "[ 0.4234 ]"
    val expected = MLFOutputDataTypeWrapper(List(0.4234.asInstanceOf[AnyRef]))

    val outputJson = parse(outputString).toOption
    outputJson.isDefined should be (true)

    val decoded = decoder.decodeJson(outputJson.get).toOption

    decoded.isDefined should be (true)
    decoded.get should equal (expected)
  }

  private def createTestsSampleSignature(types: List[TypingResult]): ModelSignature = {
    val outputs = types.map(SignatureType)

    val indexed_outputs = outputs
      .zipWithIndex
      .map { case (signatureType, i) => SignatureField(SignatureName(s"output_$i"), signatureType) }

    ModelSignature(List(), indexed_outputs)
  }
}
