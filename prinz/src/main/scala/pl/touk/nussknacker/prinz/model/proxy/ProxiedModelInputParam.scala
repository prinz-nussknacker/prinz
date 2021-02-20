package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.proxy.ProxiedModelInputParam.ParamSupplier

case class ProxiedModelInputParam(paramName: String, paramSupplier: ParamSupplier)

object ProxiedModelInputParam {

  type ParamSupplier = () => AnyRef
}
