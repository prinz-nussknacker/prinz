package pl.touk.nussknacker.prinz.model.mlflow

import pl.touk.nussknacker.prinz.UnitIntegrationTest
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.api.{LocalMLFModelLocationStrategy, MLFRegisteredModel}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestRunId
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFRestClient
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepository
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureName, SignatureType}

class MLFContainerTest extends UnitIntegrationTest {

  "Mlflow container" should "list some models" in {
    val repository = MLFRepository()
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.exists(_.nonEmpty) shouldBe true
  }

  it should "list at least two different models" in {
    val repository = MLFRepository()
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.get.groupBy(_.getName).size should be > 1
  }

  it should "list model run info with artifact location" in {
    val client = MLFRestClient(MLFConfig.serverUrl)
    val repository = MLFRepository()
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

    signature.getOutputType.size should equal (1)
    signature.getOutputType.head should equal (SignatureType("double"))

    signature.getInputDefinition.size should equal (expectedSignatureInput.size)
    expectedSignatureInput.map(input)
      .foreach(signature.getInputDefinition.contains(_) shouldBe true)
  }

  it should "allow to run model with sample data" in {
    val instance = getModelInstance().get
    val signature = instance.getSignature
    val sampleInput = sampleInputForSignature(signature)

    instance.run(signature.getSignatureNames.map(_.name), sampleInput).isRight shouldBe true
  }

  it should "have models that returns different values for the same input" in {
    val instances = List(getModelInstance(_.head).get, getModelInstance(_.last).get)
    val signatures = instances.map(_.getSignature)
    val sampleInputs = signatures.map(sampleInputForSignature)

    (instances, signatures, sampleInputs)
      .zipped
      .map { case (instance, signature, input) => instance.run(signature.getSignatureNames.map(_.name), input) }
      .map(_.right.get)
      .groupBy(_.toString())
      .size should be > 1
  }

  private def getModelInstance(extract: List[MLFRegisteredModel] => MLFRegisteredModel = _.head) = {
    val repository = MLFRepository()
    val model = repository.listModels.toOption.map(extract)
    model.map(_.toModelInstance)
  }

  private def input(definition: (String, String)) =
    (SignatureName(definition._1), SignatureType(definition._2))

  private def sampleInputForSignature(signature: ModelSignature) =
    List(Seq.tabulate(signature.getInputDefinition.size)(_.toDouble).toList)
}
