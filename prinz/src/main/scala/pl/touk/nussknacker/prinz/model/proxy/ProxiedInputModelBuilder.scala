package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.proxy.ProxiedModelComposedInputParam.{ComposedParamsSupplier, ParamsExtractor}
import pl.touk.nussknacker.prinz.model.proxy.ProxiedModelInputParam.ParamSupplier
import pl.touk.nussknacker.prinz.model.{Model, SignatureName}

import scala.collection.mutable

class ProxiedInputModelBuilder(private val model: Model) {

  protected val params: mutable.Map[SignatureName, ProxiedModelInputParam] = mutable.Map[SignatureName, ProxiedModelInputParam]()

  protected val composedParams: mutable.MutableList[ProxiedModelComposedInputParam[_ <: AnyRef]] = mutable.MutableList()

  def proxyParam(paramName: String)(paramSupplier: ParamSupplier): this.type = {
    val signatureName = SignatureName(paramName)
    val createdInputParam = ProxiedModelInputParam(signatureName, paramSupplier)
    params put(signatureName, createdInputParam)
    this
  }

  def proxyComposedParam[T <: AnyRef](paramSupplier: ComposedParamsSupplier[T],
                                      paramsExtractor: ParamsExtractor[T]): this.type = {
    val createdInputParam = ProxiedModelComposedInputParam(paramSupplier,paramsExtractor)
    composedParams += createdInputParam
    this
  }

  def build(): ProxiedInputModel = new ProxiedInputModel(model, params.values.toList, composedParams.toList)
}

object ProxiedInputModelBuilder {

  def apply(model: Model): ProxiedInputModelBuilder = new ProxiedInputModelBuilder(model)
}
