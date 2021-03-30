package pl.touk.nussknacker.prinz.pmml

import com.dimafeng.testcontainers.WaitingForService
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import pl.touk.nussknacker.prinz.UnitIntegrationTest
import pl.touk.nussknacker.prinz.UnitIntegrationTest.EnvMap
import pl.touk.nussknacker.prinz.pmml.PMMLUnitIntegrationTest.{PMML_HTTP_SERVER_READY_REGEX, PMML_SAMPLES_SERVICE_NAME, TIMEOUT_MINUTES}

import java.io.File
import java.time.Duration

abstract class PMMLUnitIntegrationTest extends UnitIntegrationTest {

  override def dockerComposeFile: File = new File("../dev-environment/docker-compose-pmml.yaml")

  override def env: EnvMap = List(
    "PMML_SERVER_PORT",
    "PMML_SAMPLES_PORT",
  ).map(readEnv).toMap

  override def waitingForService: Option[WaitingForService] = Some(
    WaitingForService(PMML_SAMPLES_SERVICE_NAME, new LogMessageWaitStrategy()
      .withRegEx(PMML_HTTP_SERVER_READY_REGEX)
      .withStartupTimeout(Duration.ofMinutes(TIMEOUT_MINUTES)))
  )
}

object PMMLUnitIntegrationTest {

  private val TIMEOUT_MINUTES = 5

  private val PMML_HTTP_SERVER_READY_REGEX = ".*Serving.*"

  private val PMML_SAMPLES_SERVICE_NAME = "pmml-samples"
}
