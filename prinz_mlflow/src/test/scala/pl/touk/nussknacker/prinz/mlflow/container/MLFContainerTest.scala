package pl.touk.nussknacker.prinz.mlflow.container

import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.BeforeAndAfterAll
import pl.touk.nussknacker.prinz.{H2Database, TestH2IdTransformedParamProvider, UnitIntegrationTest}
import pl.touk.nussknacker.prinz.UnitIntegrationTest.STATIC_SERVER_PATH
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.converter.MLFSignatureInterpreter
import pl.touk.nussknacker.prinz.mlflow.model.api.{MLFModelInstance, MLFRegisteredModel}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.MLFRestRunId
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.{MLFRestClient, MLFRestClientConfig}
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository
import pl.touk.nussknacker.prinz.model.proxy.api.ProxiedInputModel
import pl.touk.nussknacker.prinz.model.proxy.build.{ProxiedHttpInputModelBuilder, ProxiedInputModelBuilder}
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureField, SignatureName, SignatureType}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import java.sql.ResultSet
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.FiniteDuration

class MLFContainerTest extends UnitIntegrationTest
  with BeforeAndAfterAll {

  private implicit val config: Config = ConfigFactory.load()

  private implicit val mlfConfig: MLFConfig = MLFConfig()

  "Mlflow container" should "list some models" in {
    val repository = new MLFModelRepository
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.exists(_.nonEmpty) shouldBe true
  }

  it should "list at least two different models" in {
    val repository = new MLFModelRepository
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.get.groupBy(_.getName).size should be > 1
  }

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
    val inputNames = signature.getInputNames
    val outputNames = signature.getOutputNames

    outputNames.size should equal (1)
    signature.getOutputValueType(outputNames.head) should equal (Some(SignatureType(MLFSignatureInterpreter.fromMLFDataType("double"))))

    inputNames.size should equal (expectedSignatureInput.size)
    expectedSignatureInput.map(input)
      .foreach(field => inputNames.contains(field.signatureName) shouldBe true)
    expectedSignatureInput.map(input)
      .foreach(field => signature.getInputValueType(field.signatureName) should equal (Some(field.signatureType)))
  }

  it should "allow to run model with sample data" in {
    val instance = getModelInstance().get
    val signature = instance.getSignature
    val sampleInput = constructInputMap(0.415.asInstanceOf[AnyRef], signature)
    val awaitTimeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe true
  }

  it should "have models that returns different values for the same input" in {
    val instances = List(
      getModelInstance(getElasticnetWineModelModel(1)).get,
      getModelInstance(getElasticnetWineModelModel(2)).get
    )
    val signatures = instances.map(_.getSignature)
    val sampleInputs = signatures.map(constructInputMap(0.235.asInstanceOf[AnyRef], _))
    val awaitTimeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

    (instances, sampleInputs)
      .zipped
      .map { case (instance, input) => instance.run(input) }
      .map { future => Await.result(future, awaitTimeout) }
      .map(_.right.get)
      .groupBy(_.toString())
      .size should be > 1
  }

  it should "have fraud detection model" in {
    val instance = getModelInstance(getFraudDetectionModel)

    instance.isDefined shouldBe true
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

    outputNames.size should equal (1)
    signature.getOutputValueType(outputNames.head) should equal (Some(SignatureType(MLFSignatureInterpreter.fromMLFDataType("integer"))))

    inputNames.size should equal (expectedSignatureInput.size)
    expectedSignatureInput.map(input)
      .foreach(field => inputNames.contains(field.signatureName) shouldBe true)
    expectedSignatureInput.map(input)
      .foreach(field => signature.getInputValueType(field.signatureName) should equal (Some(field.signatureType)))
  }

  it should "allow to run fraud model with sample data" in {
    val instance = getModelInstance(getFraudDetectionModel).get
    val sampleInput = VectorMultimap(
      ("age", "4"),
      ("gender", "F"),
      ("category", "es_transportation"),
      ("amount", 800.0),
    ).mapValues(_.asInstanceOf[AnyRef])
    val awaitTimeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe (true)
  }

  it should "allow to run fraud model with http proxied data" in {
    val model = getModel(getFraudDetectionModel).get
    val proxiedModel = new ProxiedHttpInputModelBuilder(model)
      .proxyHttpGet("amount", s"$STATIC_SERVER_PATH/double")
      .proxyHttpGet("gender", s"$STATIC_SERVER_PATH/string")
      .build()
    val instance = proxiedModel.toModelInstance
    val sampleInput = VectorMultimap(
      ("age", "4"),
      ("category", "es_transportation"),
    ).mapValues(_.asInstanceOf[AnyRef])
    val awaitTimeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe (true)
  }

  it should "allow to run fraud model with database composed proxied data" in {
    H2Database.executeUpdate("create table input_data (" +
      "id int not null," +
      "amount double not null," +
      "gender varchar(16) not null" +
      ");")
    H2Database.executeUpdate("insert into input_data values (0, 42.42, 'F')")

    val model = getModel(getFraudDetectionModel).get
    val proxiedModel = new ProxiedInputModelBuilder(model)
      .proxyComposedParam[ResultSet](
        _ => Future(H2Database.executeNonEmptyQuery("select * from input_data where id = 0;")),
        rs => extractResultSetValues(rs, List(
          ("amount", _.getBigDecimal("amount")),
          ("gender", _.getString("gender"))
        ))
      )
      .build()
    val instance = proxiedModel.toModelInstance
    val sampleInput = VectorMultimap(
      ("age", "4"),
      ("category", "es_transportation"),
    ).mapValues(_.asInstanceOf[AnyRef])
    val awaitTimeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe (true)
  }

  it should "allow to run fraud model with database transformed proxied data" in {
    val tableName = "customer"
    prepareCustomerTestData()
    val model = getModel(getFraudDetectionModel).get
    val paramProvider = new TestH2IdTransformedParamProvider(tableName)
    val proxiedModel = ProxiedInputModel(model, paramProvider)
    val instance = proxiedModel.toModelInstance
    val sampleInput = VectorMultimap(
      (s"${tableName}_id", 1),
      ("age", "4"),
      ("category", "es_transportation"),
    ).mapValues(_.asInstanceOf[AnyRef])
    val awaitTimeout = FiniteDuration(1000, TimeUnit.MILLISECONDS)

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe (true)
  }

  it should "transform model param definition with database transformed proxied data" in {
    val tableName = "customer"
    prepareCustomerTestData()

    val model = getModel(getFraudDetectionModel).get
    val paramProvider = new TestH2IdTransformedParamProvider(tableName)
    val proxiedModel = ProxiedInputModel(model, paramProvider)
    val instance = proxiedModel.toModelInstance
    val enricherInputsDefinition = instance.getParameterDefinition.signatureInputs
    val inputsNames = enricherInputsDefinition.map(_.signatureName.name)

    inputsNames should contain (s"${tableName}_id")
    inputsNames should contain ("age")
    inputsNames should contain ("category")
    inputsNames should not contain (s"${tableName}_gender")
    inputsNames should not contain (s"gender")
    inputsNames should not contain (s"${tableName}_amount")
    inputsNames should not contain (s"amount")
  }

  private def prepareCustomerTestData(): Unit = {
    val tableName = "customer"
    H2Database.executeUpdate(s"drop table if exists $tableName;")
    H2Database.executeUpdate(s"create table $tableName (" +
      s"${tableName}_id int not null," +
      s"${tableName}_amount double not null," +
      s"${tableName}_gender varchar(16) not null" +
      ");")
    H2Database.executeUpdate(s"insert into $tableName values " +
      "(0, 42.42, 'F')," +
      "(1, 24.24, 'M')," +
      "(2, 22.22, 'M')," +
      "(3, 44.44, 'F');")
  }

  private def extractResultSetValues(rs: ResultSet, extracts: List[(String, ResultSet => AnyRef)]): Future[Iterable[(SignatureName, AnyRef)]] =
    Future(extracts.map(colExtract => (SignatureName(colExtract._1), colExtract._2(rs))))

  private def getModel(extract: List[MLFRegisteredModel] => MLFRegisteredModel = getElasticnetWineModelModel(1)): Option[MLFRegisteredModel] = {
    val repository = new MLFModelRepository
    repository.listModels.toOption.map(extract)
  }

  private def getModelInstance(extract: List[MLFRegisteredModel] => MLFRegisteredModel = getElasticnetWineModelModel(1)): Option[MLFModelInstance] = {
    val model = getModel(extract)
    model.map(_.toModelInstance)
  }

  private def input(definition: (String, String)) =
    SignatureField(SignatureName(definition._1), SignatureType(MLFSignatureInterpreter.fromMLFDataType(definition._2)))

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
