package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.ModelName

case class ProxyInputModelName(proxiedModelName: ModelName)
  extends ModelName(s"[proxied] ${proxiedModelName.internal}")
