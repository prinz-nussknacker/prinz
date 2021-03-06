package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.{ModelMetadata, SignatureName}
import pl.touk.nussknacker.prinz.model.proxy.ProxiedModelInputParam.ParamSupplier

import java.util.Objects
import scala.concurrent.Future

final case class ProxiedModelInputParam(paramName: SignatureName,
                                        paramSupplier: ParamSupplier)

object ProxiedModelInputParam {

  type ParamSupplier = ModelInputParamMetadata => Future[AnyRef]
}
