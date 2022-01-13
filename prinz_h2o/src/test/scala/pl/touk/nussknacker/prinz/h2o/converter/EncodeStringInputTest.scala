package pl.touk.nussknacker.prinz.h2o.converter

import pl.touk.nussknacker.engine.api.typed.typing.Typed
import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureField, SignatureName, SignatureType}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

class EncodeStringInputTest extends UnitTest {

  it should "wrap string inputs into single quotes" in {
    val signature = ModelSignature(
      List(SignatureField(SignatureName("string_name"), SignatureType(Typed[String]))),
      List()
    )
    val inputMap = VectorMultimap(
      ("string_name", "Bob".asInstanceOf[AnyRef])
    )
    val expectedConvertedInputMap = VectorMultimap(
      ("string_name", "'Bob'".asInstanceOf[AnyRef])
    )

    val convertedInput = H2ODataConverter.inputToTypedModelInput(inputMap, signature)

    convertedInput should equal (expectedConvertedInputMap)
  }

  it should "not wrap string inputs in extra quotes when already wrapped single quotes" in {
    val signature = ModelSignature(
      List(SignatureField(SignatureName("string_name"), SignatureType(Typed[String]))),
      List()
    )
    val inputMap = VectorMultimap(
      ("string_name", "'Bob'".asInstanceOf[AnyRef])
    )
    val expectedConvertedInputMap = VectorMultimap(
      ("string_name", "'Bob'".asInstanceOf[AnyRef])
    )

    val convertedInput = H2ODataConverter.inputToTypedModelInput(inputMap, signature)

    convertedInput should equal (expectedConvertedInputMap)
  }

  it should "wrap string inputs with missing quotes" in {
    val signature = ModelSignature(
      List(SignatureField(SignatureName("string_name"), SignatureType(Typed[String])),
        SignatureField(SignatureName("string_surname"), SignatureType(Typed[String]))),
      List()
    )
    val inputMap = VectorMultimap(
      ("string_name", "'Bob".asInstanceOf[AnyRef]),
      ("string_surname", "King'".asInstanceOf[AnyRef])
    )
    val expectedConvertedInputMap = VectorMultimap(
      ("string_surname", "'King'".asInstanceOf[AnyRef]),
        ("string_name", "'Bob'".asInstanceOf[AnyRef])
    )

    val convertedInput = H2ODataConverter.inputToTypedModelInput(inputMap, signature)

    convertedInput should equal (expectedConvertedInputMap)
  }
}
