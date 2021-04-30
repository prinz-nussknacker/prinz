package pl.touk.nussknacker.prinz.util.repository

import java.net.URI

trait RepositoryClientConfig {

  val fileExtension: String

  val modelsDirectory: URI

  val modelDirectoryHrefSelector: Option[String]
}
