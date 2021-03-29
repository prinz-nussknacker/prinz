package pl.touk.nussknacker.prinz

import com.dimafeng.testcontainers.DockerComposeContainer.{ComposeFile, Def}
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.dimafeng.testcontainers.{ContainerDef, WaitingForService}
import pl.touk.nussknacker.prinz.UnitIntegrationTest.{BRIDGE_NET_NAME, EnvMap}

import java.io.File
import scala.sys.process.Process

abstract class UnitIntegrationTest extends UnitTest with TestContainerForAll {

  def dockerComposeFile: File

  def env: EnvMap

  def waitingForService: Option[WaitingForService]

  override def startContainers(): containerDef.Container = {
    Process(s"docker network rm $BRIDGE_NET_NAME").!
    val res = Process(s"docker network create $BRIDGE_NET_NAME").!
    if(res != 0) {
      throw new IllegalStateException("Non 0 code returned from docker network create command.")
    }
    super.startContainers()
  }

  override val containerDef: ContainerDef = Def(
    composeFiles = ComposeFile(Left(dockerComposeFile)),
    env = env,
    waitingFor = waitingForService
  )

  protected def readEnv(name: String): (String, String) = (name, sys.env.getOrElse(name, ""))
}

object UnitIntegrationTest {

  private val BRIDGE_NET_NAME = "dev-bridge-net"

  type EnvMap = Map[String, String]
}
