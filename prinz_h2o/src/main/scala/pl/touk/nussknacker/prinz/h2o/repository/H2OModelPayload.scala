package pl.touk.nussknacker.prinz.h2o.repository

import java.io.InputStream

// Tip: MOJO readers also support java.net.URL in place of InputStream
case class H2OModelPayload(inputStream: InputStream, name: String, version: String)
