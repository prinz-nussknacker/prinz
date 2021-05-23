package pl.touk.nussknacker.prinz.util.exceptions

abstract class PrinzException(message: String, cause: Throwable) extends Exception(message, cause) {

  override def toString: String = s"${this.getClass.getSimpleName}: ${this.getMessage}"

  def this(message: String) = this(message, new Exception(message))

  def this(cause: Throwable) = this(cause.getMessage, cause)
}
