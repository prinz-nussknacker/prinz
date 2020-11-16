package pl.touk.nussknacker.prinz.model.repository

case class ModelRepositoryException(message: String) extends Exception {

  override def toString: String = s"[Model Repository error] ${this.getClass.getSimpleName}: ${this.message}"
}
