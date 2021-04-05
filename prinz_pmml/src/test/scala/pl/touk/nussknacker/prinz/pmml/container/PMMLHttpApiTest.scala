package pl.touk.nussknacker.prinz.pmml.container

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.prinz.container.ApiIntegrationSpec
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.pmml.repository.HttpPMMLModelRepository
import pl.touk.nussknacker.prinz.pmml.{PMMLConfig, PMMLContainerUnitTest}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class PMMLHttpApiTest extends PMMLContainerUnitTest with ApiIntegrationSpec {

  private implicit val config: Config = ConfigFactory.load()

  private implicit val pmmlConfig: PMMLConfig = PMMLConfig()

  override def integrationName: String = "PMML http server"

  override def getRepository: ModelRepository = new HttpPMMLModelRepository

  override def awaitTimeout: FiniteDuration = FiniteDuration(100, TimeUnit.MILLISECONDS)
}
