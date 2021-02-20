package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.ModelMetadata
import pl.touk.nussknacker.prinz.model.proxy.ProxiedModelInputParam.ParamSupplier

case class ProxiedModelInputParam(paramName: String,
                                  paramSupplier: ParamSupplier,
                                  overwriteProvided: Boolean = false)

case class ModelInputParamMetadata(paramName: String, modelMetadata: ModelMetadata)

object ProxiedModelInputParam {

  type ParamSupplier = ModelInputParamMetadata => AnyRef
}
