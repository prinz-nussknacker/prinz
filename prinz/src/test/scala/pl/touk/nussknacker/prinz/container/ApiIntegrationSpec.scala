package pl.touk.nussknacker.prinz.container

import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureField, SignatureName, SignatureType}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.Await

trait ApiIntegrationSpec extends UnitTest with TestModelsManager {

  s"$integrationName " should "list some models" in {
    val repository = getRepository
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.exists(_.nonEmpty) shouldBe true
  }

  it should "list at least two different models" in {
    val repository = getRepository
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.get.groupBy(_.getMetadata.modelName).size should be > 1
  }

  it should "have model instance available" in {
    val instance = getModelInstance()

    instance.isDefined shouldBe true
  }

  it should "have model that has signature defined" in {
    val model = getModel()
    val signature = model.map(_.getMetadata.signature)

    signature.isDefined shouldBe true
  }

  it should "allow to run model with sample data" in {
    val model = getModel().get
    val instance = getModelInstance().get
    val signature = model.getMetadata.signature
    val sampleInput = constructInputMap(0.415.asInstanceOf[AnyRef], signature)

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe true
  }

  it should "have models that returns different values for the same input" in {
    val models = List(
      getModel(getElasticnetWineModelModel(1)).get,
      getModel(getElasticnetWineModelModel(2)).get
    )
    val instances = List(
      getModelInstance(getElasticnetWineModelModel(1)).get,
      getModelInstance(getElasticnetWineModelModel(2)).get
    )
    val signatures = models.map(_.getMetadata.signature)
    val sampleInputs = signatures.map(constructInputMap(0.235.asInstanceOf[AnyRef], _))

    (instances, sampleInputs)
      .zipped
      .map { case (instance, input) => instance.run(input) }
      .map { future => Await.result(future, awaitTimeout) }
      .map(_.right.get)
      .groupBy(_.toString())
      .size should be > 1
  }

  it should "have fraud detection model" in {
    val instance = getModelInstance(getFraudDetectionModel)

    instance.isDefined shouldBe true
  }

  it should "allow to run fraud model with sample data" in {
    val instance = getModelInstance(getFraudDetectionModel).get
    val sampleInput = VectorMultimap(
      ("age", "4"),
      ("gender", "F"),
      ("category", "es_transportation"),
      ("amount", 800.0),
    ).mapValues(_.asInstanceOf[AnyRef])

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe true
  }

  it should "have wine model with proper model signature" in {
    val model = getModel(getElasticnetWineModelModel(1))
    val signature = model.map(_.getMetadata.signature).get
    val inputNames = signature.getSignatureInputs.map(_.signatureName)
    val outputNames = signature.getSignatureOutputs.map(_.signatureName)

    assertSignatureFields(inputNames, expectedWineInputs)(signature.getInputValueType)
    assertSignatureFields(outputNames, expectedWineOutputs)(signature.getOutputValueType)
  }

  it should "have fraud detection model with proper model signature" in {
    val model = getModel(getFraudDetectionModel)
    val signature = model.map(_.getMetadata.signature).get
    val inputNames = signature.getSignatureInputs.map(_.signatureName)
    val outputNames = signature.getSignatureOutputs.map(_.signatureName)

    assertSignatureFields(inputNames, expectedFraudInputs)(signature.getInputValueType)
    assertSignatureFields(outputNames, expectedFraudOutputs)(signature.getOutputValueType)
  }

  def expectedWineInputs: List[(String, TypingResult)]

  def expectedWineOutputs: List[(String, TypingResult)]

  def expectedFraudInputs: List[(String, TypingResult)]

  def expectedFraudOutputs: List[(String, TypingResult)]

  private def assertSignatureFields(names: List[SignatureName], expected: List[(String, TypingResult)])
                                   (typeExtractor: SignatureName => Option[SignatureType]): Unit = {
    names.size should equal(expected.size)
    expected.map(field)
      .foreach(field => names.contains(field.signatureName) shouldBe true)
    expected.map(field)
      .foreach(field => typeExtractor(field.signatureName) should equal(Some(field.signatureType)))
  }

  private def field(definition: (String, TypingResult)) =
    SignatureField(SignatureName(definition._1), SignatureType(definition._2))
}
