package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.Model
import pl.touk.nussknacker.prinz.model.proxy.ProxiedModelInputParam.ParamSupplier

import scala.collection.mutable

class ProxiedInputModelBuilder(private val model: Model) {

  private val params = mutable.Map[String, ProxiedModelInputParam]()

  def proxyParam(paramName: String,  overwriteProvided: Boolean = false)(paramSupplier: ParamSupplier): Unit = {
    val createdInputParam = ProxiedModelInputParam(paramName, paramSupplier, overwriteProvided)
    params put (paramName, createdInputParam)
  }

  def build(): ProxiedInputModel =
    new ProxiedInputModel(model, params.values)
}

object ProxiedInputModelBuilder {

  def apply(model: Model): ProxiedModelInputParam = new ProxiedInputModelBuilder(model)
}
