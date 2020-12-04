package pl.touk.nussknacker.prinz.converter.mlflow

import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.util.collection.immutable.SetMultimap
import pl.touk.nussknacker.prinz.util.converter.mlflow.MlflowDataConverter

class MlflowDataConverterTest extends UnitTest {

  val converter: MlflowDataConverter = MlflowDataConverter()

  implicit class EitherOps[A](private val obj: A) {

    def asLeft[B]: Either[A, B] = Left(obj)
    def asRight[B]: Either[B, A] = Right(obj)
  }

  "MlflowDataConverter" should "convert empty data input" in {
    val inputData = """{
                      |    "columns": [],
                      |    "data": []
                      |}""".stripMargin

    val expected = SetMultimap.empty[String, Either[BigDecimal, String]]

    converter.toMultimap(inputData) should equal (expected)
  }

  it should "convert data in split format" in {
    val inputData = """{
                      |    "columns": ["a", "b", "c"],
                      |    "data": [[1, 2, 3], [4, 5, 6]]
                      |}""".stripMargin

    val expected = SetMultimap[String, Either[BigDecimal, String]](
      "a" -> BigDecimal(1).asLeft[String], "a" -> BigDecimal(4).asLeft[String],
      "b" -> BigDecimal(2).asLeft[String], "b" -> BigDecimal(5).asLeft[String],
      "c" -> BigDecimal(3).asLeft[String], "c" -> BigDecimal(6).asLeft[String],
    )

    converter.toMultimap(inputData) should equal (expected)
  }

  it should "throw IllegalArgumentException for null input" in {
    val inputData: String = null

    assertThrows[IllegalArgumentException] {
      converter.toMultimap(inputData)
    }
  }
}
