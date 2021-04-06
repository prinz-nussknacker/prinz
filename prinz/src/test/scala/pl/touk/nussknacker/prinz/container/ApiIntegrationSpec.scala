package pl.touk.nussknacker.prinz.container

import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.model.{SignatureField, SignatureName, SignatureType}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.Await

trait ApiIntegrationSpec extends UnitTest with TestModelsManger {

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
    models.get.groupBy(_.getName).size should be > 1
  }

  it should "have model instance available" in {
    val instance = getModelInstance()

    instance.isDefined shouldBe true
  }

  it should "have model instance that has signature defined" in {
    val instance = getModelInstance()
    val signature = instance.map(_.getSignature)

    signature.isDefined shouldBe true
  }

  it should "allow to run model with sample data" in {
    val instance = getModelInstance().get
    val signature = instance.getSignature
    val sampleInput = constructInputMap(0.415.asInstanceOf[AnyRef], signature)

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe true
  }

  it should "have models that returns different values for the same input" in {
    val instances = List(
      getModelInstance(getElasticnetWineModelModel(1)).get,
      getModelInstance(getElasticnetWineModelModel(2)).get
    )
    val signatures = instances.map(_.getSignature)
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
    val expectedSignatureInput = List(
      ("fixed acidity", Typed[Double]),
      ("volatile acidity", Typed[Double]),
      ("citric acid", Typed[Double]),
      ("residual sugar", Typed[Double]),
      ("chlorides", Typed[Double]),
      ("free sulfur dioxide", Typed[Double]),
      ("total sulfur dioxide", Typed[Double]),
      ("density", Typed[Double]),
      ("pH", Typed[Double]),
      ("sulphates", Typed[Double]),
      ("alcohol", Typed[Double])
    )
    val instance = getModelInstance(getElasticnetWineModelModel(1))
    val signature = instance.map(_.getSignature).get
    val inputNames = signature.getInputNames
    val outputNames = signature.getOutputNames

    outputNames.size should equal(1)
    signature.getOutputValueType(outputNames.head) should equal(Some(SignatureType(Typed[Double])))

    inputNames.size should equal(expectedSignatureInput.size)
    expectedSignatureInput.map(input)
      .foreach(field => inputNames.contains(field.signatureName) shouldBe true)
    expectedSignatureInput.map(input)
      .foreach(field => signature.getInputValueType(field.signatureName) should equal(Some(field.signatureType)))
  }

  it should "have fraud detection model with proper model signature" in {
    val expectedSignatureInput = List(
      ("age", Typed[String]),
      ("gender", Typed[String]),
      ("category", Typed[String]),
      ("amount", Typed[Double])
    )
    val instance = getModelInstance(getFraudDetectionModel)
    val signature = instance.map(_.getSignature).get
    val inputNames = signature.getInputNames
    val outputNames = signature.getOutputNames

    outputNames.size should equal(1)
    signature.getOutputValueType(outputNames.head) should equal(Some(SignatureType(Typed[Int])))

    inputNames.size should equal(expectedSignatureInput.size)
    expectedSignatureInput.map(input)
      .foreach(field => inputNames.contains(field.signatureName) shouldBe true)
    expectedSignatureInput.map(input)
      .foreach(field => signature.getInputValueType(field.signatureName) should equal(Some(field.signatureType)))
  }

  private def input(definition: (String, TypingResult)) =
    SignatureField(SignatureName(definition._1), SignatureType(definition._2))
}
