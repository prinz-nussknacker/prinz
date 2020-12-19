package pl.touk.nussknacker.prinz.mlflow.converter

import cats.implicits.catsSyntaxEitherId
import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepository
import pl.touk.nussknacker.prinz.model.ModelInstance

class MLFDataConverterTest extends UnitTest {

  "MlflowDataConverter" should "convert empty data input" in {
    val bothEmpty = """{
                      |    "columns": [],
                      |    "data": []
                      |}""".stripMargin

    val dataEmpty = """{
                      |    "columns": ["a", "b", "c"],
                      |    "data": []
                      |}""".stripMargin

    val expected = VectorMultimap.empty[String, Either[BigDecimal, String]]

    MLFDataConverter.toMultimap(bothEmpty) should equal (expected)
    MLFDataConverter.toMultimap(dataEmpty) should equal (expected)
  }

  it should "convert data in split format and maintain ordering" in {
    val inputData = """{
                      |    "columns": ["a", "b", "c"],
                      |    "data": [[1, 2, 3], [4, 5, 6]]
                      |}""".stripMargin

    val expected = VectorMultimap[String, Int](
      "a" -> 1, "a" -> 4,
      "b" -> 2, "b" -> 5,
      "c" -> 3, "c" -> 6,
    )

    val wrongDataOrdering = VectorMultimap[String, Int](
      "a" -> 4, "a" -> 1,
      "b" -> 5, "b" -> 2,
      "c" -> 6, "c" -> 3,
    )

    val converted = MLFDataConverter.toMultimap(inputData)
    val expectedDecimals = expected.mapValues(value => BigDecimal(value).asLeft[String])
    val wrongDataOrderingDecimals = wrongDataOrdering.mapValues(value => BigDecimal(value).asLeft[String])

    converted should equal (expectedDecimals)
    converted should not equal wrongDataOrderingDecimals
  }

  it should "throw IllegalArgumentException for wrong input" in {
    val wrongNumberOfColumns = """{
                      |    "columns": ["a", "b", "c"],
                      |    "data": [[1, 2], [4, 5]]
                      |}""".stripMargin

    val invalidColumnNames = """{
                               |    "columns": [a, b, c],
                               |    "data": [[1, 2, 3], [4, 5, 6]]
                               |}""".stripMargin

    val missingColumns = """{
                           |    "data": [[1, 2, 3], [4, 5, 6]]
                           |}""".stripMargin


    val missingData = """{
                        |    "columns": [a, b, c],
                        |}""".stripMargin


    assertThrows[IllegalArgumentException] {
      MLFDataConverter.toMultimap(wrongNumberOfColumns)
    }
    assertThrows[IllegalArgumentException] {
      MLFDataConverter.toMultimap(invalidColumnNames)
    }
    assertThrows[IllegalArgumentException] {
      MLFDataConverter.toMultimap(missingColumns)
    }
    assertThrows[IllegalArgumentException] {
      MLFDataConverter.toMultimap(missingData)
    }
  }
}
