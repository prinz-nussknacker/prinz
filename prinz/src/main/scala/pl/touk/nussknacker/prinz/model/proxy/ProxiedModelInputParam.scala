package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.{ModelMetadata, SignatureName}
import pl.touk.nussknacker.prinz.model.proxy.ProxiedModelInputParam.ParamSupplier

import java.util.Objects

final case class ProxiedModelInputParam(paramName: SignatureName,
                                        paramSupplier: ParamSupplier,
                                        overwriteProvided: Boolean = false) {
  override def hashCode(): Int =
    Objects.hashCode(paramName)

  override def equals(obj: Any): Boolean = obj match {
    case that: ProxiedModelInputParam => Objects.equals(paramName, that.paramName)
    case _: Any => false
  }
}

case class ModelInputParamMetadata(paramName: SignatureName, modelMetadata: ModelMetadata)

object ProxiedModelInputParam {

  type ParamSupplier = ModelInputParamMetadata => AnyRef
}
