package pl.touk.nussknacker.prinz.util.repository.client

import pl.touk.nussknacker.prinz.util.repository.payload.ModelPayload

import java.io.{FileInputStream, InputStream}
import java.net.URI
import java.nio.file.Paths

class LocalFSRepositoryClient(fileExtension: String) extends AbstractRepositoryClient(fileExtension) {

  override protected def loadModelsOnPath(path: URI): Iterable[ModelPayload] = {
    val file = Paths.get(path).toFile
    if(file.isFile) {
      List(ModelPayload(path))
    }
    else {
      file.listFiles().filter(_.isFile)
        .filter(f => isValidFile(f.toURI))
        .map(f => ModelPayload(f.toURI))
    }
  }

  override def openModelFile(path: URI): InputStream = new FileInputStream(Paths.get(path).toFile)
}
