package pl.touk.nussknacker.prinz.util.http

final case class RestClientException(message: String) extends Exception {

  override def toString: String = s"${getClass.getSimpleName}: ${this.message}"
}
