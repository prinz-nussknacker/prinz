package pl.touk.nussknacker.prinz

import java.io.File

import com.dimafeng.testcontainers.DockerComposeContainer.{ComposeFile, Def}
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.dimafeng.testcontainers.{ContainerDef, WaitingForService}
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import pl.touk.nussknacker.prinz.UnitIntegrationTest.{DOCKER_COMPOSE_FILE, MLFLOW_SERVER_SERVICE_NAME, MODEL_SERVED_READY_REGEX}

abstract class UnitIntegrationTest extends UnitTest with TestContainerForAll {

  override val containerDef: ContainerDef = Def(
    ComposeFile(Left(DOCKER_COMPOSE_FILE)),
    waitingFor = Some(
      WaitingForService(MLFLOW_SERVER_SERVICE_NAME, new LogMessageWaitStrategy()
        .withRegEx(MODEL_SERVED_READY_REGEX))
    )
  )
}

object UnitIntegrationTest {

  private val DOCKER_COMPOSE_FILE = new File("../dev-environment/docker-compose.yaml")

  private val MODEL_SERVED_READY_REGEX = ".*Listening at.*"

  private val MLFLOW_SERVER_SERVICE_NAME = "mlflow-server"
}
