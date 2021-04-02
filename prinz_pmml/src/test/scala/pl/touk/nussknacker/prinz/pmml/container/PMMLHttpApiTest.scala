package pl.touk.nussknacker.prinz.pmml.container

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.prinz.TestModelsManger
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.pmml.repository.HttpPMMLModelRepository
import pl.touk.nussknacker.prinz.pmml.{PMMLConfig, PMMLUnitIntegrationTest}

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

class PMMLHttpApiTest extends PMMLUnitIntegrationTest with TestModelsManger {

  private implicit val config: Config = ConfigFactory.load()

  private implicit val pmmlConfig: PMMLConfig = PMMLConfig()

  private val awaitTimeout = FiniteDuration(100, TimeUnit.MILLISECONDS)

  "PMML http server " should "list some models" in {
    val repository = getRepository
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.exists(_.nonEmpty) shouldBe true
  }

  it should "list at least two different models" in {
    val repository = getRepository
    val models = repository.listModels.toOption

    models.isDefined shouldBe true
    models.get.groupBy(_.getName).size should be > 1
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

  it should "allow to run model with sample data" in {
    val instance = getModelInstance().get
    val signature = instance.getSignature
    val sampleInput = constructInputMap(0.415.asInstanceOf[AnyRef], signature)

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

    (instances, sampleInputs)
      .zipped
      .map { case (instance, input) => instance.run(input) }
      .map { future => Await.result(future, awaitTimeout) }
      .map(_.right.get)
      .groupBy(_.toString())
      .size should be > 1
  }

  override def getRepository: ModelRepository = new HttpPMMLModelRepository
}
