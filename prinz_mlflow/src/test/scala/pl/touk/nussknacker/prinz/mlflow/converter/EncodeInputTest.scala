package pl.touk.nussknacker.prinz.mlflow.converter

import io.circe.syntax.EncoderOps
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureField, SignatureName, SignatureType}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

class EncodeInputTest extends UnitTest {

  it should "encode input into JSON" in {
    val expectedJson2Spaces = """{
                                 |  "columns" : [
                                 |    "input_0",
                                 |    "input_1",
                                 |    "input_2",
                                 |    "input_3"
                                 |  ],
                                 |  "data" : [
                                 |    [
                                 |      0.525,
                                 |      "sample input",
                                 |      32.6235,
                                 |      true
                                 |    ]
                                 |  ]
                                 |}""".stripMargin

    val signature = createIndexedInputSignature(
      List(Typed[Double], Typed[String], Typed[Double], Typed[Boolean])
    )
    val input = List(
      0.525.asInstanceOf[AnyRef],
      "sample input".asInstanceOf[AnyRef],
      32.6235.asInstanceOf[AnyRef],
      true.asInstanceOf[AnyRef]
    )
    val map = createInputMultimap(input, signature)

    val dataframe = MLFDataConverter.inputToDataframe(map, signature)
    val json2Spaces = dataframe.asJson.spaces2

    json2Spaces should equal (expectedJson2Spaces)
  }

  it should "encode multiple input into JSON" in {
    val expectedJsonString2Spaces = """{
                                      |  "columns" : [
                                      |    "input_0",
                                      |    "input_1",
                                      |    "input_2",
                                      |    "input_3"
                                      |  ],
                                      |  "data" : [
                                      |    [
                                      |      0.525,
                                      |      "sample input",
                                      |      32.6235,
                                      |      true
                                      |    ],
                                      |    [
                                      |      14.15,
                                      |      "testing",
                                      |      104.23,
                                      |      false
                                      |    ],
                                      |    [
                                      |      -28.55,
                                      |      "multiple columns",
                                      |      2.35,
                                      |      true
                                      |    ]
                                      |  ]
                                      |}""".stripMargin

    val signature = createIndexedInputSignature(
      List(Typed[Double], Typed[String], Typed[Float], Typed[Boolean])
    )

    val input1 = List(0.525, 14.15, -28.55)
      .map(_.asInstanceOf[AnyRef])
    val input2 = List("sample input", "testing", "multiple columns")
      .map(_.asInstanceOf[AnyRef])
    val input3 = List(32.6235, 104.23, 2.35)
      .map(_.asInstanceOf[AnyRef])
    val input4 = List(true, false, true)
      .map(_.asInstanceOf[AnyRef])

    val map = buildMultipleInput(List(input1, input2, input3, input4), signature)

    val dataframe = MLFDataConverter.inputToDataframe(map, signature)
    val json2Spaces = dataframe.asJson.spaces2

    json2Spaces should equal (expectedJsonString2Spaces)
  }

  private def createIndexedInputSignature(inputs: List[TypingResult]): ModelSignature = {
    val inputTypes = inputs.map(SignatureType)
    val input = inputTypes
      .zipWithIndex
      .map { case (signatureType, i) => SignatureField(SignatureName(s"input_$i"), signatureType) }

    ModelSignature(input, List())
  }

  private def createInputMultimap(input: List[AnyRef], signature: ModelSignature): VectorMultimap[String, AnyRef] = {
    VectorMultimap(signature.getInputNames.map(_.name).zip(input))
  }

  private def buildMultipleInput(inputs: List[List[AnyRef]], signature: ModelSignature): VectorMultimap[String, AnyRef] = {
    val colValuesList = signature.getInputNames.map(_.name).zip(inputs)
    val colValues = colValuesList.flatMap { case (col, colValues) => colValues.map((col, _)) }
    VectorMultimap(colValues)
  }
}
