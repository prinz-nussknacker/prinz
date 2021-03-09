package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.proxy.ProxiedModelComposedInputParam.{ComposedParamsSupplier, ParamsExtractor}
import pl.touk.nussknacker.prinz.model.{ModelMetadata, SignatureName}

import scala.concurrent.Future

final case class ProxiedModelComposedInputParam[T](paramsSupplier: ComposedParamsSupplier[T],
                                                   paramsExtractor: ParamsExtractor[T])

object ProxiedModelComposedInputParam {

  type ComposedParamsSupplier[+T] = ModelMetadata => Future[T]

  type ParamsExtractor[-T] = T => Future[Iterable[(SignatureName, _ <: AnyRef)]]
}
