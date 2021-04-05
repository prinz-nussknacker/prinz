package pl.touk.nussknacker.prinz.pmml

import com.dimafeng.testcontainers.WaitingForService
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import pl.touk.nussknacker.prinz.container.ContainerUnitTest
import pl.touk.nussknacker.prinz.container.ContainerUnitTest.{EnvMap, readEnv, readEnvWithName}
import pl.touk.nussknacker.prinz.pmml.PMMLContainerUnitTest.{PMML_HTTP_SERVER_READY_REGEX, PMML_SAMPLES_SERVICE_NAME, TIMEOUT_MINUTES}

import java.io.File
import java.time.Duration

abstract class PMMLContainerUnitTest extends ContainerUnitTest {

  override def dockerComposeFile: File = new File(s"${readEnv("REPOSITORY_ABSOLUTE_ROOT")}/dev-environment/docker-compose-pmml.yaml")

  override def env: EnvMap = List(
    "PMML_SERVER_PORT",
    "PMML_SAMPLES_PORT",
    "PMML_NGINX_STATIC_PORT",
  ).map(readEnvWithName).toMap

  override def waitingForService: Option[WaitingForService] = Some(
    WaitingForService(PMML_SAMPLES_SERVICE_NAME, new LogMessageWaitStrategy()
      .withRegEx(PMML_HTTP_SERVER_READY_REGEX)
      .withStartupTimeout(Duration.ofMinutes(TIMEOUT_MINUTES)))
  )
}

object PMMLContainerUnitTest {
modelDirectoryHrefSelector
  private val TIMEOUT_MINUTES = 5

  private val PMML_HTTP_SERVER_READY_REGEX = ".*Training models finished.*"

  private val PMML_SAMPLES_SERVICE_NAME = "pmml-samples"

  val STATIC_SERVER_PATH = s"http://localhost:${readEnv("PMML_NGINX_STATIC_PORT")}/static"
}
