package pl.touk.nussknacker.prinz.util.exceptions

abstract class PrinzException(message: String) extends Exception {

  override def toString: String = s"${this.getClass.getSimpleName}: ${this.message}"
}
