package pl.touk.nussknacker.prinz.converter.mlflow

import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.util.collection.immutable.MultiTreeMap

class MlflowDataConverterTest extends UnitTest {

  "MlflowDataConverter" should "convert empty data input" in {

  }

  it should "convert data in map format" in {
    val inputData = """[
                      |    {"a": 1,"b": 2,"c": 3},
                      |    {"a": 4,"b": 5,"c": 6}
                      |]""".stripMargin

    val expected = MultiTreeMap[String, Int](
      "a" -> 1, "a" -> 4,
      "b" -> 2, "b" -> 5,
      "c" -> 3, "c" -> 6,
    )

    // TODO call converted method and check if expected data is returned
  }

  it should "convert data in list format" in {
    val inputData = """{
                      |    "columns": ["a", "b", "c"],
                      |    "data": [[1, 2, 3], [4, 5, 6]]
                      |}""".stripMargin

    val expected = MultiTreeMap[String, Int](
      "a" -> 1, "a" -> 4,
      "b" -> 2, "b" -> 5,
      "c" -> 3, "c" -> 6,
    )
    // TODO call converted method and check if expected data is returned
  }

  it should "throw IllegalArgumentException for null input" in {
    val inputData: String = null // scalastyle:ignore

    assertThrows[IllegalArgumentException] {
      // TODO call converter with null argument
      throw new IllegalArgumentException("Make it thrown by converter on bad input")
    }
  }
}
