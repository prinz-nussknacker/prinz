package pl.touk.nussknacker.prinz.container.mlflow

import java.net.URL

import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepository

class MlflowContainerTest extends UnitTest {

  "Mlflow container" should "list some models" in {
    val repository = MLFRepository(url("http://localhost:5000"))
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

    assert(instance.map(_.getSignature).isDefined)
  }

  private def getModelInstance = {
    val repository = MLFRepository(url("http://localhost:5000"))
    val model = repository.listModels.toOption.head.head
    model.getLatestVersion.getModelInstance
  }

  private def url(url: String) = new URL(url)
}
