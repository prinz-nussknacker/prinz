package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.proxy.ProxiedModelCompositeInputParam.{ComposedParamsSupplier, ParamsExtractor}
import pl.touk.nussknacker.prinz.model.{ModelMetadata, SignatureName}
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx

import scala.concurrent.Future

final case class ProxiedModelCompositeInputParam[T](paramsSupplier: ComposedParamsSupplier[T],
                                                    paramsExtractor: ParamsExtractor[T]) {

  def supplyCompositeParamValues(modelMetadata: ModelMetadata): Future[Iterable[(String, AnyRef)]] = {
    paramsSupplier(modelMetadata)
      .flatMap(paramsExtractor(_))
      .map { iter => iter.map { case (name, value) => (name.name, value) } }
  }
}

object ProxiedModelCompositeInputParam {

  type ComposedParamsSupplier[+T] = ModelMetadata => Future[T]

  type ParamsExtractor[-T] = T => Future[Iterable[(SignatureName, _ <: AnyRef)]]
}
