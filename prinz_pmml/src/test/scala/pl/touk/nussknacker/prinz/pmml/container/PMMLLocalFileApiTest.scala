package pl.touk.nussknacker.prinz.pmml.container

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.prinz.container.ApiIntegrationSpec
import pl.touk.nussknacker.prinz.container.ContainerUnitTest.readEnv
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.pmml.PMMLContainerUnitTest.STATIC_SERVER_PATH
import pl.touk.nussknacker.prinz.pmml.repository.{HttpPMMLModelRepository, LocalFilePMMLModelRepository}
import pl.touk.nussknacker.prinz.pmml.{PMMLConfig, PMMLContainerUnitTest}
import pl.touk.nussknacker.prinz.proxy.ModelsProxySpec

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class PMMLLocalFileApiTest extends PMMLContainerUnitTest
  with ApiIntegrationSpec
  with ModelsProxySpec {

  private implicit val config: Config = ConfigFactory.parseString(
    s"""
      |  pmml {
      |    modelsDirectory: "file:${readEnv("REPOSITORY_ABSOLUTE_ROOT")}/dev-environment/pmml-samples/exports"
      |  }
      |""".stripMargin)

  private implicit val pmmlConfig: PMMLConfig = PMMLConfig()

  override def integrationName: String = "PMML local file"

  override def getRepository: ModelRepository = new LocalFilePMMLModelRepository

  override def awaitTimeout: FiniteDuration = FiniteDuration(500, TimeUnit.MILLISECONDS)

  override def staticServerPath: String = STATIC_SERVER_PATH
}
