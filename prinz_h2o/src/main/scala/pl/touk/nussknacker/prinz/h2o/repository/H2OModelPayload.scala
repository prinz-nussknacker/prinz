package pl.touk.nussknacker.prinz.h2o.repository

import java.io.InputStream

// Tip: MOJO readers also support java.net.URL in place of InputStream
class H2OModelPayload(var inputStream: InputStream, var name: String, var version: String)
