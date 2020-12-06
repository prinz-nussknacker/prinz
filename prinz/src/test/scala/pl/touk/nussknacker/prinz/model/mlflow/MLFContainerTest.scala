package pl.touk.nussknacker.prinz.model.mlflow

import pl.touk.nussknacker.prinz.UnitIntegrationTest
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepository
import pl.touk.nussknacker.prinz.model.{SignatureName, SignatureType}

class MLFContainerTest extends UnitIntegrationTest {

  "Mlflow container" should "list some models" in {
    val repository = MLFRepository(MLFConfig.serverUrl)
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.exists(_.nonEmpty) shouldBe true
  }

  it should "have model instance available" in {
    val instance = getModelInstance

    instance.isDefined shouldBe true
  }

  it should "have model instance that has signature defined" in {
    val instance = getModelInstance
    val signature = instance.map(_.getSignature)

    signature.isDefined shouldBe true
  }

  it should "have specific for tests model signature" in {
    val expectedSignatureInput = List(
      ("fixed acidity", "double"),
      ("volatile acidity", "double"),
      ("citric acid", "double"),
      ("residual sugar", "double"),
      ("chlorides", "double"),
      ("free sulfur dioxide", "double"),
      ("total sulfur dioxide", "double"),
      ("density", "double"),
      ("pH", "double"),
      ("sulphates", "double"),
      ("alcohol", "double")
    )
    val instance = getModelInstance
    val signature = instance.map(_.getSignature).get

    signature.getOutputType.size should equal (1)
    signature.getOutputType.head should equal (SignatureType("double"))

    signature.getInputDefinition.size should equal (expectedSignatureInput.size)
    expectedSignatureInput.map(input)
      .foreach(signature.getInputDefinition.contains(_) shouldBe true)
  }

  private def getModelInstance = {
    val repository = MLFRepository(MLFConfig.serverUrl)
    val model = repository.listModels.toOption.map(_.head)
    model.map(_.toModelInstance)
  }

  private def input(definition: (String, String)) =
    (SignatureName(definition._1), SignatureType(definition._2))
}
