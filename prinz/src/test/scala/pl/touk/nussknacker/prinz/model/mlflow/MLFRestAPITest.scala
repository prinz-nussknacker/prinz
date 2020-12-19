package pl.touk.nussknacker.prinz.model.mlflow

import pl.touk.nussknacker.prinz.UnitIntegrationTest
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.{MLFRestRegisteredModel, MLFRestRunId}
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFRestClient

class MLFRestAPITest extends UnitIntegrationTest {

  "Mlflow REST API" should "list some models" in {
    val client = getClient
    val models = listModelsOrThrow(client)

    models.length should be > 0
  }

  it should "get model run info with artifact location" in {
    val client = getClient
    val model = listModelsOrThrow(client).head
    val latestModelVersion = model.latest_versions.head
    val runInfo = client.getRunInfo(MLFRestRunId(latestModelVersion.run_id)).toOption

    runInfo.isDefined shouldBe true
    runInfo.get.info.artifact_uri.length should be > 0
  }

  private def getClient: MLFRestClient = MLFRestClient(MLFConfig.serverUrl)

  private def listModelsOrThrow(client: MLFRestClient): List[MLFRestRegisteredModel] = {
    val models = client.listModels()
    if (models.isLeft) {
      throw new AssertionError(models.left.get.message)
    }
    else {
      models.right.get
    }
  }
}
