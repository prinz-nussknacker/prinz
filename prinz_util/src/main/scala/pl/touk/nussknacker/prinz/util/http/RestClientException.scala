package pl.touk.nussknacker.prinz.util.http

final case class RestClientException(cause: Throwable) extends Exception(cause) {

  override def toString: String = s"${getClass.getSimpleName}: ${this.getMessage}"

  def this(message: String) {
    this(new Exception(message))
  }
}
