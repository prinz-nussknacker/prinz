package pl.touk.nussknacker.prinz.model.mlflow

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.prinz.UnitIntegrationTest
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.api.{MLFRegisteredModel, MLFSignatureProvider}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestRunId
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.{MLFRestClient, MLFRestClientConfig}
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepository
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureField, SignatureName, SignatureType}
import java.util.concurrent.TimeUnit

import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

class MLFContainerTest extends UnitIntegrationTest {

  private implicit val config: Config = ConfigFactory.load()

  private implicit val mlfConfig: MLFConfig = MLFConfig()

  private val interpreter: MLFSignatureProvider = MLFSignatureProvider(mlfConfig)

  "Mlflow container" should "list some models" in {
    val repository = new MLFRepository
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.exists(_.nonEmpty) shouldBe true
  }

  it should "list at least two different models" in {
    val repository = new MLFRepository
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.get.groupBy(_.getName).size should be > 1
  }

  it should "list model run info with artifact location" in {
    val client = MLFRestClient(MLFRestClientConfig.fromMLFConfig(mlfConfig))
    val repository = new MLFRepository
    val modelRunId = repository
      .listModels.toOption.get.head
      .latestVersions.head.runId
    val runInfo = client.getRunInfo(MLFRestRunId(modelRunId)).toOption

    runInfo.isDefined shouldBe true
    runInfo.map(_.info).map(_.artifact_uri).isDefined shouldBe true
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

  /*it should "have specific for tests model signature" in {
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

    outputNames.size should equal (1)
    signature.getOutputValueType(outputNames.head) should equal (Some(SignatureType(interpreter.fromMLFDataType("double"))))

    inputNames.size should equal (expectedSignatureInput.size)
    expectedSignatureInput.map(input)
      .foreach(field => inputNames.contains(field.signatureName) shouldBe true)
    expectedSignatureInput.map(input)
      .foreach(field => signature.getInputValueType(field.signatureName) should equal (Some(field.signatureType)))
  }*/

  it should "allow to run model with sample data" in {
    val instance = getModelInstance().get
    val signature = instance.getSignature
    val sampleInput = constructInputMap(0.415.asInstanceOf[AnyRef], signature)
    val awaitTimeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

    val response = Await.ready(instance.run(sampleInput), awaitTimeout)
    response.value.isDefined shouldBe true
  }

  /*it should "have models that returns different values for the same input" in {
    val instances = List(
      getModelInstance(getElasticnetWineModelModel(1)).get,
      getModelInstance(getElasticnetWineModelModel(2)).get
    )
    val signatures = instances.map(_.getSignature)
    val sampleInputs = signatures.map(sampleInputForSignature)
    val awaitTimeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

    (instances, signatures, sampleInputs)
      .zipped
      .map { case (instance, signature, input) => instance.run(signature.getInputNames.map(_.name), input) }
      .map { future => Await.result(future, awaitTimeout) }
      .map(_.right.get)
      .groupBy(_.toString())
      .size should be > 1
  }*/

  it should "have fraud detection model" in {
    val instance = getModelInstance(getFraudDetectionModel)

    instance.isDefined shouldBe true
  }

  private def getModelInstance(extract: List[MLFRegisteredModel] => MLFRegisteredModel = getElasticnetWineModelModel(1)) = {
    val repository = new MLFRepository
    val model = repository.listModels.toOption.map(extract)
    model.map(_.toModelInstance)
  }

  /*private def input(definition: (String, String)) =
    SignatureField(SignatureName(definition._1), SignatureType(interpreter.fromMLFDataType(definition._2)))*/

  private def sampleInputForSignature(signature: ModelSignature) =
    List(Seq.tabulate(signature.getInputNames.size)(_.toDouble).toList)

  private def getFraudDetectionModel: List[MLFRegisteredModel] => MLFRegisteredModel =
    models => models.filter(_.name.name.startsWith("FraudDetection")).head

  private def getElasticnetWineModelModel(modelId: Int): List[MLFRegisteredModel] => MLFRegisteredModel =
    models => models.filter(_.name.name.startsWith("ElasticnetWineModel-" + modelId)).head

  private def constructInputMap(value: AnyRef, signature: ModelSignature): VectorMultimap[String, AnyRef] = {
    val names = signature.getInputNames.map(_.name)
    val data = List.fill(names.length)(value)
    VectorMultimap(names.zip(data))
  }
}
