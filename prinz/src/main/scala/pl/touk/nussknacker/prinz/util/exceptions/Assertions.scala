package pl.touk.nussknacker.prinz.util.exceptions

object Assertions {

  @inline def assertIllegal(check: Boolean, message: String): Unit = if (!check) {
    throw new IllegalArgumentException(message)
  }
}
