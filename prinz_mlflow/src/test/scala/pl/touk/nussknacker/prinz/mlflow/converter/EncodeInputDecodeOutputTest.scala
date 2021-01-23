package pl.touk.nussknacker.prinz.mlflow.converter


import io.circe.Json
import io.circe.syntax.EncoderOps
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureField, SignatureName, SignatureType}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

class EncodeInputDecodeOutputTest extends UnitTest {

  it should "encode input into JSON" in {
    val jsonString = """{
                       |   "columns" : [
                       |      "input_a",
                       |      "input_b",
                       |      "input_c",
                       |      "input_d"
                       |   ],
                       |   "data" : [
                       |       [ 0.525 ],
                       |       [ "sample input" ],
                       |       [ 32.6235 ],
                       |       [ true ]
                       |   ]
                       |}""".stripMargin
    val expectedJson = Json.fromString(jsonString)

    val signature = createSampleSignature
    val input = List(
      0.525.asInstanceOf[AnyRef],
      "sample input".asInstanceOf[AnyRef],
      32.6235.asInstanceOf[AnyRef],
      true.asInstanceOf[AnyRef]
    )
    val map = createInputMultimap(input, signature)

    val dataframe = MLFDataConverter.inputToDataframe(map, signature)
    val json = dataframe.asJson

    json should equal (expectedJson)
  }

  it should "encode multiple input into JSON" in {
    val jsonString = """{
                       |   "columns":[
                       |      "input_a",
                       |      "input_b",
                       |      "input_c",
                       |      "input_d"
                       |   ],
                       |   "data":[
                       |      [
                       |         0.525,
                       |         14.15,
                       |         -28.55
                       |      ],
                       |      [
                       |         "sample input",
                       |         "testing",
                       |         "multiple columns"
                       |      ],
                       |      [
                       |         32.6235,
                       |         104.23,
                       |         2.35
                       |      ],
                       |      [
                       |         true,
                       |         false,
                       |         true
                       |      ]
                       |   ]
                       |}""".stripMargin
    val expectedJson = Json.fromString(jsonString)

    val signature = createSampleSignature
    val input1 = List(
      0.525.asInstanceOf[AnyRef],
      "sample input".asInstanceOf[AnyRef],
      32.6235.asInstanceOf[AnyRef],
      true.asInstanceOf[AnyRef]
    )
    val input2 = List(
      14.15.asInstanceOf[AnyRef],
      "testing".asInstanceOf[AnyRef],
      104.23.asInstanceOf[AnyRef],
      false.asInstanceOf[AnyRef]
    )
    val input3 = List(
      (-28.55).asInstanceOf[AnyRef],
      "multiple columns".asInstanceOf[AnyRef],
      2.35.asInstanceOf[AnyRef],
      true.asInstanceOf[AnyRef]
    )

    val map = buildMultipleInput(List(input1, input2, input3), signature)

    val dataframe = MLFDataConverter.inputToDataframe(map, signature)
    val json = dataframe.asJson

    json should equal (expectedJson)
  }

  it should "decode output from JSON" in {
    val signature = createSampleSignature

    val outputString = "[[ 0.4234, \"output\"]]"
    val outputJson = Json.fromString(outputString)

    val decoder = MLFOutputDataTypeWrapper.getDecoderForSignature(signature)
    val decoded = outputJson.as[MLFOutputDataTypeWrapper](decoder)

    val expected = MLFOutputDataTypeWrapper(List(0.4234.asInstanceOf[AnyRef], "output".asInstanceOf[AnyRef]))

    decoded should equal (expected)
  }

  private def createSampleSignature: ModelSignature = {
    val doubleSig = SignatureType(Typed[Double])
    val stringSig = SignatureType(Typed[String])
    val boolSig = SignatureType(Typed[Boolean])

    val input = List(
      SignatureField(SignatureName("input_a"), doubleSig),
      SignatureField(SignatureName("input_b"), stringSig),
      SignatureField(SignatureName("input_c"), doubleSig),
      SignatureField(SignatureName("input_d"), boolSig)
    )
    val output = List(
      SignatureField(SignatureName("output_a"), doubleSig),
      SignatureField(SignatureName("output_b"), stringSig),
    )

    ModelSignature(input, output)
  }

  private def createInputMultimap(input: List[AnyRef], signature: ModelSignature): VectorMultimap[String, AnyRef] = {
    VectorMultimap(signature.getInputNames.map(_.name).zip(input))
  }

  private def createOutputMultimap(output: List[AnyRef], signature: ModelSignature): VectorMultimap[String, AnyRef] = {
    VectorMultimap(signature.getOutputNames.map(_.name).zip(output))
  }

  private def buildMultipleInput(inputs: List[List[AnyRef]], signature: ModelSignature): VectorMultimap[String, AnyRef] = {
    inputs.map(createInputMultimap(_, signature))
      .foldLeft(VectorMultimap.empty[String, AnyRef])
      { case(multimap, map) => multimap.addAll(map) }
  }
}
