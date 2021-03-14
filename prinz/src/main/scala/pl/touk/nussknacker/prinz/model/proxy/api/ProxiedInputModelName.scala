package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.{Model, ModelName}

abstract sealed class ProxiedInputModelName(proxiedModel: Model, operation: String)
  extends ModelName(s"[proxied-by-$operation] ${proxiedModel.getName.internal}")

case class CompositeProxiedInputModelName(proxiedModel: Model)
  extends ProxiedInputModelName(proxiedModel, "composite")

case class TransformedProxiedInputModelName(proxiedModel: Model)
  extends ProxiedInputModelName(proxiedModel, "transform")