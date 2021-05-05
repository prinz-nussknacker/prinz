package pl.touk.nussknacker.prinz.h2o

import com.dimafeng.testcontainers.WaitingForService
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import pl.touk.nussknacker.prinz.container.ContainerUnitTest
import pl.touk.nussknacker.prinz.container.ContainerUnitTest.{EnvMap, readEnv, readEnvWithName}
import H2OContainerUnitTest.{H2O_FRAUD_MODEL_READY_REGEX, PMML_SAMPLES_SERVICE_NAME, TIMEOUT_MINUTES}

import java.io.File
import java.time.Duration

abstract class H2OContainerUnitTest extends ContainerUnitTest {

  override def dockerComposeFile: File = new File(s"${readEnv("REPOSITORY_ABSOLUTE_ROOT")}/dev-environment/docker-compose-h2o.yaml")

  override def env: EnvMap = List(
    "H2O_SERVER_PORT",
    "H2O_SAMPLES_PORT",
    "H2O_NGINX_STATIC_PORT",
  ).map(readEnvWithName).toMap

  override def waitingForService: Option[WaitingForService] = Some(
    WaitingForService(PMML_SAMPLES_SERVICE_NAME, new LogMessageWaitStrategy()
      .withRegEx(H2O_FRAUD_MODEL_READY_REGEX)
      .withStartupTimeout(Duration.ofMinutes(TIMEOUT_MINUTES)))
  )
}

object H2OContainerUnitTest {

  private val TIMEOUT_MINUTES = 5

  private val H2O_FRAUD_MODEL_READY_REGEX = ".*Fraud detection model exported.*"

  private val PMML_SAMPLES_SERVICE_NAME = "h2o-server"

  val STATIC_SERVER_PATH = s"http://localhost:${readEnv("H2O_NGINX_STATIC_PORT")}/static"
}
