package pl.touk.nussknacker.prinz.model.proxy.build

import pl.touk.nussknacker.prinz.model.proxy.api.ProxiedInputModel
import pl.touk.nussknacker.prinz.model.proxy.composite.ProxiedModelCompositeInputParam.{ComposedParamsSupplier, ParamsExtractor}
import pl.touk.nussknacker.prinz.model.proxy.composite.ProxiedModelInputParam.ParamSupplier
import pl.touk.nussknacker.prinz.model.proxy.composite.{ProxiedModelCompositeInputParam, ProxiedModelInputParam}
import pl.touk.nussknacker.prinz.model.{Model, SignatureName}

import scala.collection.mutable

class ProxiedInputModelBuilder(private val model: Model) {

  protected val params: mutable.Map[SignatureName, ProxiedModelInputParam] = mutable.Map[SignatureName, ProxiedModelInputParam]()

  protected val composedParams: mutable.MutableList[ProxiedModelCompositeInputParam[_ <: Any]] = mutable.MutableList()

  def proxyParam(paramName: String)(paramSupplier: ParamSupplier): this.type = {
    val signatureName = SignatureName(paramName)
    val createdInputParam = ProxiedModelInputParam(signatureName, paramSupplier)
    params put(signatureName, createdInputParam)
    this
  }

  def proxyComposedParam[T <: Any](paramSupplier: ComposedParamsSupplier[T],
                                      paramsExtractor: ParamsExtractor[T],
                                      proxiedParams: Iterable[SignatureName]): this.type = {
    val createdInputParam = ProxiedModelCompositeInputParam(paramSupplier,paramsExtractor, proxiedParams)
    composedParams += createdInputParam
    this
  }

  def build(): ProxiedInputModel = new ProxiedInputModel(model, params.values.toList, composedParams.toList)
}

object ProxiedInputModelBuilder {

  def apply(model: Model): ProxiedInputModelBuilder = new ProxiedInputModelBuilder(model)
}
