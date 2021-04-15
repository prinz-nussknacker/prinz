package pl.touk.nussknacker.prinz.mlflow.container

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.engine.api.typed.typing
import pl.touk.nussknacker.engine.api.typed.typing.Typed
import pl.touk.nussknacker.prinz.MLFContainerUnitTest
import pl.touk.nussknacker.prinz.MLFContainerUnitTest.STATIC_SERVER_PATH
import pl.touk.nussknacker.prinz.container.ApiIntegrationSpec
import pl.touk.nussknacker.prinz.container.ContainerUnitTest.readEnv
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestRunId
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.{MLFRestClient, MLFRestClientConfig}
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.proxy.ModelsProxySpec

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class MLFRestApiTest extends MLFContainerUnitTest
  with ApiIntegrationSpec
  with ModelsProxySpec {

  private implicit val config: Config = ConfigFactory.parseString(
    s"""
       |  mlflow {
       |    serverUrl: "http://localhost:${readEnv("MLFLOW_SERVER_PORT")}"
       |    servedModelsUrl: "http://localhost:${readEnv("MLFLOW_SERVER_PORT")}"
       |    s3AccessKey: "mlflow-key"
       |    s3SecretKey: "mlflow-secret"
       |    s3Url: "http://localhost:${readEnv("MLF_NGINX_BUCKET_PORT")}"
       |    s3ModelRelativePath: "/model/MLmodel"
       |    s3BucketName: "mlflow"
       |  }
       |""".stripMargin)

  private implicit val mlfConfig: MLFConfig = MLFConfig()

  override def getRepository: ModelRepository = new MLFModelRepository

  override def integrationName: String = "MLflow"

  override def awaitTimeout: FiniteDuration = FiniteDuration(2000, TimeUnit.MILLISECONDS)

  override def staticServerPath: String = STATIC_SERVER_PATH

  it should "list model run info with artifact location" in {
    val client = MLFRestClient(MLFRestClientConfig.fromMLFConfig(mlfConfig))
    val repository = new MLFModelRepository
    val modelRunId = repository
      .listModels.toOption.get.head
      .latestVersions.head.runId
    val runInfo = client.getRunInfo(MLFRestRunId(modelRunId)).toOption

    runInfo.isDefined shouldBe true
    runInfo.map(_.info).map(_.artifact_uri).isDefined shouldBe true
  }

  override def expectedWineInputs: List[(String, typing.TypingResult)] = List(
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

  override def expectedWineOutputs: List[(String, typing.TypingResult)] = List(
    ("output_0", Typed[Double])
  )

  override def expectedFraudInputs: List[(String, typing.TypingResult)] = List(
    ("age", Typed[String]),
    ("gender", Typed[String]),
    ("category", Typed[String]),
    ("amount", Typed[Double])
  )

  override def expectedFraudOutputs: List[(String, typing.TypingResult)] = List(
    ("output_0", Typed[Int])
  )
}
