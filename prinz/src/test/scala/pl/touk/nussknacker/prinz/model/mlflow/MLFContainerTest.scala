package pl.touk.nussknacker.prinz.model.mlflow

import pl.touk.nussknacker.prinz.UnitIntegrationTest
import pl.touk.nussknacker.prinz.mlflow.MlflowConstants.DEFAULT_URL
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepository
import pl.touk.nussknacker.prinz.model.{SignatureName, SignatureType}

class MLFContainerTest extends UnitIntegrationTest {

  "Mlflow container" should "list some models" in {
    val repository = MLFRepository(DEFAULT_URL)
    val models = repository.listModels.toOption

    assert(models.isDefined)
    assert(models.exists(_.nonEmpty))
  }

  it should "have model instance available" in {
    val instance = getModelInstance

    assert(instance.isDefined)
  }

  it should "have model instance that have signature defined" in {
    val instance = getModelInstance
    val signature = instance.map(_.getSignature)

    assert(signature.isDefined)
  }

  it should "have specific for tests model signature" in {
    val instance = getModelInstance
    val signature = instance.map(_.getSignature).get

    assertResult(1)(signature.getOutputType.size)
    assertResult(SignatureType("double"))(signature.getOutputType.head)

    assertResult(3)(signature.getInputDefinition.size)
    assert(signature.getInputDefinition.contains(input("a")("double")))
    assert(signature.getInputDefinition.contains(input("b")("double")))
    assert(signature.getInputDefinition.contains(input("c")("double")))
  }

  private def getModelInstance = {
    val repository = MLFRepository(DEFAULT_URL)
    val model = repository.listModels.toOption.map(_.head)
    model.map(_.toModelInstance)
  }

  private def input(name: String)(`type`: String) =
    (SignatureName(name), SignatureType(`type`))
}