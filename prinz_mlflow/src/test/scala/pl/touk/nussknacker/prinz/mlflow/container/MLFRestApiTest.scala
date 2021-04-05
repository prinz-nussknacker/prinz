package pl.touk.nussknacker.prinz.mlflow.container

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.prinz.MLFContainerUnitTest
import pl.touk.nussknacker.prinz.MLFContainerUnitTest.STATIC_SERVER_PATH
import pl.touk.nussknacker.prinz.container.ApiIntegrationSpec
import pl.touk.nussknacker.prinz.container.ContainerUnitTest.readEnv
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.converter.MLFSignatureInterpreter
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestRunId
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.{MLFRestClient, MLFRestClientConfig}
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.model.{SignatureField, SignatureName, SignatureType}
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
       |    s3Url: "http://localhost:${readEnv("NGINX_BUCKET_PORT")}"
       |    s3ModelRelativePath: "/model/MLmodel"
       |    s3BucketName: "mlflow"
       |  }
       |""".stripMargin)

  private implicit val mlfConfig: MLFConfig = MLFConfig()

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
    val instance = getModelInstance()
    val signature = instance.map(_.getSignature).get
    val inputNames = signature.getInputNames
    val outputNames = signature.getOutputNames

    outputNames.size should equal(1)
    signature.getOutputValueType(outputNames.head) should equal(Some(SignatureType(MLFSignatureInterpreter.fromMLFDataType("double"))))

    inputNames.size should equal(expectedSignatureInput.size)
    expectedSignatureInput.map(input)
      .foreach(field => inputNames.contains(field.signatureName) shouldBe true)
    expectedSignatureInput.map(input)
      .foreach(field => signature.getInputValueType(field.signatureName) should equal(Some(field.signatureType)))
  }

  it should "have fraud detection model with proper model signature" in {
    val expectedSignatureInput = List(
      ("age", "string"),
      ("gender", "string"),
      ("category", "string"),
      ("amount", "double")
    )
    val instance = getModelInstance(getFraudDetectionModel)
    val signature = instance.map(_.getSignature).get
    val inputNames = signature.getInputNames
    val outputNames = signature.getOutputNames

    outputNames.size should equal(1)
    signature.getOutputValueType(outputNames.head) should equal(Some(SignatureType(MLFSignatureInterpreter.fromMLFDataType("integer"))))

    inputNames.size should equal(expectedSignatureInput.size)
    expectedSignatureInput.map(input)
      .foreach(field => inputNames.contains(field.signatureName) shouldBe true)
    expectedSignatureInput.map(input)
      .foreach(field => signature.getInputValueType(field.signatureName) should equal(Some(field.signatureType)))
  }

  override def getRepository: ModelRepository = new MLFModelRepository

  override def integrationName: String = "MLflow"

  override def awaitTimeout: FiniteDuration = FiniteDuration(2000, TimeUnit.MILLISECONDS)

  override def staticServerPath: String = STATIC_SERVER_PATH

  private def input(definition: (String, String)) =
    SignatureField(SignatureName(definition._1), SignatureType(MLFSignatureInterpreter.fromMLFDataType(definition._2)))
}
