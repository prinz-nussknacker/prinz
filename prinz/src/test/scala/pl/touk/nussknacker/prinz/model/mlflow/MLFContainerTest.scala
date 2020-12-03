package pl.touk.nussknacker.prinz.model.mlflow

import pl.touk.nussknacker.prinz.UnitIntegrationTest
import pl.touk.nussknacker.prinz.mlflow.MlflowConstants.DEFAULT_URL
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepository
import pl.touk.nussknacker.prinz.model.{SignatureName, SignatureType}

class MLFContainerTest extends UnitIntegrationTest {

  "Mlflow container" should "list some models" in {
    val repository = MLFRepository(DEFAULT_URL)
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
    val instance = getModelInstance
    val signature = instance.map(_.getSignature).get

    signature.getOutputType.size should equal (1)
    signature.getOutputType.head should equal (SignatureType("double"))

    signature.getInputDefinition.size should equal (3)
    signature.getInputDefinition.contains(input("a")("double")) shouldBe true
    signature.getInputDefinition.contains(input("b")("double")) shouldBe true
    signature.getInputDefinition.contains(input("c")("double")) shouldBe true
  }

  private def getModelInstance = {
    val repository = MLFRepository(DEFAULT_URL)
    val model = repository.listModels.toOption.map(_.head)
    model.map(_.toModelInstance)
  }

  private def input(name: String)(inputType: String) =
    (SignatureName(name), SignatureType(inputType))
}
