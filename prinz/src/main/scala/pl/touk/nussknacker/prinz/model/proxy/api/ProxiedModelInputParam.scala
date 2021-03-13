package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.proxy.api
import pl.touk.nussknacker.prinz.model.proxy.api.ProxiedModelInputParam.ParamSupplier
import pl.touk.nussknacker.prinz.model.{ModelMetadata, SignatureName}

import scala.concurrent.Future

final case class ProxiedModelInputParam(paramName: SignatureName,
                                        paramSupplier: ParamSupplier) {

  def supplyParamValue(modelMetadata: ModelMetadata): Future[(String, AnyRef)] = {
    val metadata = api.ModelInputParamMetadata(paramName, modelMetadata)
    val resultFuture = paramSupplier(metadata)
    resultFuture.map { result =>
      (paramName.name, result)
    }
  }
}

object ProxiedModelInputParam {

  type ParamSupplier = ModelInputParamMetadata => Future[AnyRef]
}
