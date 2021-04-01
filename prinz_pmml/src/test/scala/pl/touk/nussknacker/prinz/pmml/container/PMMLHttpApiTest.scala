package pl.touk.nussknacker.prinz.pmml.container

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.prinz.pmml.repository.HttpPMMLModelRepository
import pl.touk.nussknacker.prinz.pmml.{PMMLConfig, PMMLUnitIntegrationTest}

class PMMLHttpApiTest extends PMMLUnitIntegrationTest {

  private implicit val config: Config = ConfigFactory.load()

  private implicit val pmmlConfig: PMMLConfig = PMMLConfig()

  "PMML http server " should "list some models" in {
    val repository = new HttpPMMLModelRepository
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.exists(_.nonEmpty) shouldBe true
  }

  it should "list at least two different models" in {
    val repository = new HttpPMMLModelRepository
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.get.groupBy(_.getName).size should be > 1
  }
}
