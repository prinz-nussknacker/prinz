package pl.touk.nussknacker.prinz.h2o.repository

import java.io.InputStream

// Tip: MOJO readers also support java.net.URL in place of InputStream
final case class H2OModelPayload(inputStream: InputStream, name: String, version: String)

object H2OModelPayload {

  def apply(inputStream: InputStream, name: String, version: String): H2OModelPayload = {
    H2OModelPayload(inputStream, name, version)
  }

}
