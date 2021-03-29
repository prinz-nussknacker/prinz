package pl.touk.nussknacker.prinz

import java.io.File
import java.time.Duration
import com.dimafeng.testcontainers.WaitingForService
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import pl.touk.nussknacker.prinz.MLFUnitIntegrationTest.{
  MLFLOW_SERVER_SERVICE_NAME, MODEL_1_SERVED_READY_REGEX, MODEL_2_SERVED_READY_REGEX,
  MODEL_3_SERVED_READY_REGEX, TIMEOUT_MINUTES, readEnvWithName}
import pl.touk.nussknacker.prinz.UnitIntegrationTest.EnvMap

abstract class MLFUnitIntegrationTest extends UnitIntegrationTest {

  override def dockerComposeFile: File = new File("../dev-environment/docker-compose-mlflow.yaml")

  override def env: EnvMap = List(
    "MODEL_1_PORT",
    "MODEL_2_PORT",
    "MODEL_3_PORT",
    "NGINX_STATIC_PORT",
  ).map(readEnvWithName).toMap

  override def waitingForService: Option[WaitingForService] = Some(
    WaitingForService(MLFLOW_SERVER_SERVICE_NAME, new LogMessageWaitStrategy()
      .withRegEx(MODEL_1_SERVED_READY_REGEX)
      .withRegEx(MODEL_2_SERVED_READY_REGEX)
      .withRegEx(MODEL_3_SERVED_READY_REGEX)
      .withStartupTimeout(Duration.ofMinutes(TIMEOUT_MINUTES)))
  )
}

object MLFUnitIntegrationTest {

  private val TIMEOUT_MINUTES = 5

  private val MODEL_1_SERVED_READY_REGEX = s".*Listening at.*${readEnv("MODEL_1_PORT")}.*"

  private val MODEL_2_SERVED_READY_REGEX = s".*Listening at.*${readEnv("MODEL_2_PORT")}.*"

  private val MODEL_3_SERVED_READY_REGEX = s".*Listening at.*${readEnv("MODEL_3_PORT")}.*"

  private val MLFLOW_SERVER_SERVICE_NAME = "mlflow-server"

  val STATIC_SERVER_PATH = s"http://localhost:${readEnv("NGINX_STATIC_PORT")}/static"

  private def readEnvWithName(name: String): (String, String) = (name, sys.env.getOrElse(name, ""))

  protected def readEnv(name: String): String = sys.env.getOrElse(name, "")
}
