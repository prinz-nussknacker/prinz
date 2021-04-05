package pl.touk.nussknacker.prinz.proxy

import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.{H2Database, UnitTest}
import pl.touk.nussknacker.prinz.container.TestModelsManger
import pl.touk.nussknacker.prinz.model.SignatureName
import pl.touk.nussknacker.prinz.model.proxy.api.ProxiedInputModel
import pl.touk.nussknacker.prinz.model.proxy.build.{ProxiedHttpInputModelBuilder, ProxiedInputModelBuilder}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import java.sql.ResultSet
import scala.concurrent.{Await, Future}

trait ModelsProxySpec extends UnitTest with TestModelsManger {

  def staticServerPath: String

  s"$integrationName " should "allow to run fraud model with http proxied data" in {
    val model = getModel(getFraudDetectionModel).get
    val proxiedModel = new ProxiedHttpInputModelBuilder(model)
      .proxyHttpGet("amount", s"$staticServerPath/double")
      .proxyHttpGet("gender", s"$staticServerPath/string")
      .build()
    val instance = proxiedModel.toModelInstance
    val sampleInput = VectorMultimap(
      ("age", "4"),
      ("category", "es_transportation"),
    ).mapValues(_.asInstanceOf[AnyRef])

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe true
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

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe true
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

    val response = Await.result(instance.run(sampleInput), awaitTimeout)
    response.toOption.isDefined shouldBe true
  }

  it should "transform model param definition with database transformed proxied data" in {
    val tableName = "customer"
    prepareCustomerTestData()

    val model = getModel(getFraudDetectionModel).get
    val paramProvider = new TestH2IdTransformedParamProvider(tableName)
    val proxiedModel = ProxiedInputModel(model, paramProvider)
    val instance = proxiedModel.toModelInstance
    val enricherInputsDefinition = instance.getParameterDefinition.getSignatureInputs
    val inputsNames = enricherInputsDefinition.map(_.signatureName.name)

    inputsNames should contain (s"${tableName}_id")
    inputsNames should contain ("age")
    inputsNames should contain ("category")
    inputsNames should not contain s"${tableName}_gender"
    inputsNames should not contain "gender"
    inputsNames should not contain s"${tableName}_amount"
    inputsNames should not contain s"amount"
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
}
