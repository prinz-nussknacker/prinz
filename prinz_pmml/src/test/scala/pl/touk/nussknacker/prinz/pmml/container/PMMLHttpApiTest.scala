package pl.touk.nussknacker.prinz.pmml.container

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.prinz.container.ApiIntegrationSpec
import pl.touk.nussknacker.prinz.container.ContainerUnitTest.readEnv
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.pmml.PMMLContainerUnitTest.STATIC_SERVER_PATH
import pl.touk.nussknacker.prinz.pmml.repository.HttpPMMLModelRepository
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
       |    modelsDirectory: "http://localhost:${readEnv("PMML_SAMPLES_PORT")}"
       |    modelDirectoryHrefSelector: "body > ul > li > a"
       |  }
       |""".stripMargin)

  private implicit val pmmlConfig: PMMLConfig = PMMLConfig()

  override def integrationName: String = "PMML http server"

  override def getRepository: ModelRepository = new HttpPMMLModelRepository

  override def awaitTimeout: FiniteDuration = FiniteDuration(500, TimeUnit.MILLISECONDS)

  override def staticServerPath: String = STATIC_SERVER_PATH
}
