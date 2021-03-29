package pl.touk.nussknacker.prinz.pmml

import com.dimafeng.testcontainers.WaitingForService
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import pl.touk.nussknacker.prinz.UnitIntegrationTest

import java.io.File
import java.time.Duration

abstract class PMMLUnitIntegrationTest extends UnitIntegrationTest {

  override def dockerComposeFile: File = new File("../dev-environment/docker-compose-pmml.yaml")

  override def env: Map[String, String] = List(
    "PMML_SERVER_PORT",
  ).map(readEnv).toMap

  override def waitingForService: Option[WaitingForService] = None
}
