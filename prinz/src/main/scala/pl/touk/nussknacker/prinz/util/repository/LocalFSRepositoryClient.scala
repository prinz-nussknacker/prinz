package pl.touk.nussknacker.prinz.util.repository

import java.io.{FileInputStream, InputStream}
import java.net.URI
import java.nio.file.Paths

class LocalFSRepositoryClient(fileExtension: String) extends AbstractRepositoryClient(fileExtension) {

  override protected def readPath(path: URI): Iterable[ModelPayload] = {
    val file = Paths.get(path).toFile
    if(file.isFile) {
      List(ModelPayload(path))
    }
    else {
      file.listFiles().filter(_.isFile)
        .filter(f => isCorrectFile(f.toURI))
        .map(f => ModelPayload(f.toURI))
    }
  }

  override def openModelFile(path: URI): InputStream = new FileInputStream(Paths.get(path).toFile)
}
