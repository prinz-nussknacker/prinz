package pl.touk.nussknacker.prinz

import java.io.File
import java.time.Duration
import scala.sys.process.Process

import com.dimafeng.testcontainers.DockerComposeContainer.{ComposeFile, Def}
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.dimafeng.testcontainers.{ContainerDef, WaitingForService}
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import pl.touk.nussknacker.prinz.UnitIntegrationTest.{DOCKER_COMPOSE_FILE, MLFLOW_SERVER_SERVICE_NAME,
  MODEL_1_SERVED_READY_REGEX, MODEL_2_SERVED_READY_REGEX, TIMEOUT_MINUTES}

abstract class UnitIntegrationTest extends UnitTest with TestContainerForAll {

  override def startContainers(): containerDef.Container = {
    Process("docker network create dev-bridge-net").!
    super.startContainers()
  }

  override val containerDef: ContainerDef = Def(
    composeFiles = ComposeFile(Left(DOCKER_COMPOSE_FILE)),
    waitingFor = Some(
      WaitingForService(MLFLOW_SERVER_SERVICE_NAME, new LogMessageWaitStrategy()
        .withRegEx(MODEL_1_SERVED_READY_REGEX)
        .withRegEx(MODEL_2_SERVED_READY_REGEX)
        .withStartupTimeout(Duration.ofMinutes(TIMEOUT_MINUTES)))
    )
  )
}

object UnitIntegrationTest {

  private val DOCKER_COMPOSE_FILE = new File("../dev-environment/docker-compose.yaml")

  private val TIMEOUT_MINUTES = 1

  private val MODEL_1_SERVED_READY_REGEX = s".*Listening at.*1234.*"

  private val MODEL_2_SERVED_READY_REGEX = s".*Listening at.*4321.*"

  private val MLFLOW_SERVER_SERVICE_NAME = "mlflow-server"
}
