package pl.touk.nussknacker.prinz.converter.mlflow

import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.util.collection.immutable.SetMultimap
import pl.touk.nussknacker.prinz.util.converter.mlflow.MlflowDataConverter
import cats.implicits.catsSyntaxEitherId

class MlflowDataConverterTest extends UnitTest {

  "MlflowDataConverter" should "convert empty data input" in {
    val inputData = """{
                      |    "columns": [],
                      |    "data": []
                      |}""".stripMargin

    val expected = SetMultimap.empty[String, Either[BigDecimal, String]]

    MlflowDataConverter.toMultimap(inputData) should equal (expected)
  }

  it should "convert data in split format" in {
    val inputData = """{
                      |    "columns": ["a", "b", "c"],
                      |    "data": [[1, 2, 3], [4, 5, 6]]
                      |}""".stripMargin

    val expected = SetMultimap[String, Int](
      "a" -> 1, "a" -> 4,
      "b" -> 2, "b" -> 5,
      "c" -> 3, "c" -> 6,
    )

    val decimals = for (column <- expected.keys; value <- expected.get(column).get)
      yield (column, BigDecimal(value).asLeft[String])

    val expectedWithDecimals = SetMultimap(decimals)

    MlflowDataConverter.toMultimap(inputData) should equal (expectedWithDecimals)
  }

  it should "throw IllegalArgumentException for wrong input" in {
    val inputData = """{
                      |    "columns": ["a", "b", "c"],
                      |    "data": [[1, 2], [4, 5]]
                      |}""".stripMargin

    assertThrows[IllegalArgumentException] {
      MlflowDataConverter.toMultimap(inputData)
    }
  }
}
