package pl.touk.nussknacker.prinz.container

import com.dimafeng.testcontainers.DockerComposeContainer.{ComposeFile, Def}
import com.dimafeng.testcontainers.scalatest.TestContainerForAll
import com.dimafeng.testcontainers.{ContainerDef, WaitingForService}
import pl.touk.nussknacker.prinz.UnitTest
import pl.touk.nussknacker.prinz.container.ContainerUnitTest.{BRIDGE_NET_NAME, EnvMap}

import java.io.File
import scala.sys.process.Process

trait ContainerUnitTest extends UnitTest
  with TestContainerForAll {

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

object ContainerUnitTest {

  private val BRIDGE_NET_NAME = "dev-bridge-net"

  type EnvMap = Map[String, String]
}
