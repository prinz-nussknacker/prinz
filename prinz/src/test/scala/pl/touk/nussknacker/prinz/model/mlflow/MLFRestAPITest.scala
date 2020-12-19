package pl.touk.nussknacker.prinz.model.mlflow

import pl.touk.nussknacker.prinz.UnitIntegrationTest
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestRunId
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFRestClient

class MLFRestAPITest extends UnitIntegrationTest {

  "Mlflow REST API" should "list some models" in {
    val client = getClient
    val models = client.listModels().toOption

    models.isDefined shouldBe true
    models.exists(_.nonEmpty) shouldBe true
  }

  it should "get model run info with artifact location" in {
    val client = getClient
    val model = client.listModels().toOption.get.head
    val latestModelVersion = model.latest_versions.head
    val runInfo = client.getRunInfo(MLFRestRunId(latestModelVersion.run_id)).toOption

    runInfo.isDefined shouldBe true
    runInfo.get.info.artifact_uri.length should be > 0
  }

  private def getClient = MLFRestClient(MLFConfig.serverUrl)
}
