package pl.touk.nussknacker.prinz.pmml.container

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.engine.api.typed.typing
import pl.touk.nussknacker.engine.api.typed.typing.Typed
import pl.touk.nussknacker.prinz.container.ApiIntegrationSpec
import pl.touk.nussknacker.prinz.container.ContainerUnitTest.readEnv
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.pmml.PMMLContainerUnitTest.STATIC_SERVER_PATH
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository
import pl.touk.nussknacker.prinz.pmml.{PMMLConfig, PMMLContainerUnitTest}
import pl.touk.nussknacker.prinz.proxy.ModelsProxySpec

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class PMMLHttpApiTest extends PMMLContainerUnitTest
  with ApiIntegrationSpec
  with ModelsProxySpec {

  private implicit val config: Config = ConfigFactory.parseString(
    s"""
       |  pmml {
       |    fileExtension: ".pmml"
       |    modelsDirectory: "http://localhost:${readEnv("PMML_SAMPLES_PORT")}"
       |    modelDirectoryHrefSelector: "body > ul > li > a"
       |    modelVersionSeparator: "-v"
       |  }
       |""".stripMargin)

  private implicit val pmmlConfig: PMMLConfig = PMMLConfig()

  override def integrationName: String = "PMML http server"

  override def getRepository: ModelRepository = new PMMLModelRepository

  override def awaitTimeout: FiniteDuration = FiniteDuration(500, TimeUnit.MILLISECONDS)

  override def staticServerPath: String = STATIC_SERVER_PATH

  override def expectedWineInputs: List[(String, typing.TypingResult)] = List(
    ("free sulfur dioxide", Typed[Double]),
    ("total sulfur dioxide", Typed[Double]),
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
    ("fraud", Typed[BigInt])
  )
}
