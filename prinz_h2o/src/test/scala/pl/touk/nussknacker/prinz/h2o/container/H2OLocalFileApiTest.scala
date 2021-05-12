package pl.touk.nussknacker.prinz.h2o.container

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.engine.api.typed.typing
import pl.touk.nussknacker.engine.api.typed.typing.Typed
import pl.touk.nussknacker.prinz.container.ApiIntegrationSpec
import pl.touk.nussknacker.prinz.container.ContainerUnitTest.readEnv
import pl.touk.nussknacker.prinz.h2o.{H2OConfig, H2OContainerUnitTest}
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.h2o.H2OContainerUnitTest.STATIC_SERVER_PATH
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelRepository
import pl.touk.nussknacker.prinz.proxy.ModelsProxySpec

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class H2OLocalFileApiTest extends H2OContainerUnitTest
  with ApiIntegrationSpec
  with ModelsProxySpec {

  private implicit val config: Config = ConfigFactory.parseString(
    s"""
      |  h2o {
      |    fileExtension: ".zip"
      |    modelsDirectory: "file://${readEnv("REPOSITORY_ABSOLUTE_ROOT")}/dev-environment/h2o/exports"
      |    modelDirectoryHrefSelector: ""
      |    modelVersionSeparator: "-v"
      |  }
      |""".stripMargin)

  private implicit val h2oConfig: H2OConfig = H2OConfig()

  override def integrationName: String = "H2O local file"

  override def getRepository: ModelRepository = new H2OModelRepository

  override def awaitTimeout: FiniteDuration = FiniteDuration(1000, TimeUnit.MILLISECONDS)

  override def staticServerPath: String = STATIC_SERVER_PATH

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
    ("quality", Typed[Double])
  )

  override def expectedFraudInputs: List[(String, typing.TypingResult)] = List(
    ("age", Typed[String]),
    ("gender", Typed[String]),
    ("category", Typed[String]),
    ("amount", Typed[Double])
  )

  override def expectedFraudOutputs: List[(String, typing.TypingResult)] = List(
    ("fraud", Typed[String])
  )
}
