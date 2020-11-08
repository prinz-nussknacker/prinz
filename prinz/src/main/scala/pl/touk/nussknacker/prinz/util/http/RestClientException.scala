package pl.touk.nussknacker.prinz.util.http

case class RestClientException(message: String) extends Exception {

  override def toString: String = s"${this.getClass.getSimpleName}: ${this.message}"
}
