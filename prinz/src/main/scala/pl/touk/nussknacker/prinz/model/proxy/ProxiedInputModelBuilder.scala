package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.model.{Model, SignatureName, SignatureType}
import pl.touk.nussknacker.prinz.model.proxy.ProxiedModelInputParam.ParamSupplier
import pl.touk.nussknacker.prinz.util.http.RestJsonClient

import scala.collection.mutable

class ProxiedInputModelBuilder(private val model: Model) {

  private val params = mutable.Map[SignatureName, ProxiedModelInputParam]()

  def proxyParam(paramName: String, overwriteProvided: Boolean = false)(paramSupplier: ParamSupplier): ProxiedInputModelBuilder = {
    val signatureName = SignatureName(paramName)
    val createdInputParam = ProxiedModelInputParam(signatureName, paramSupplier, overwriteProvided)
    params put (signatureName, createdInputParam)
    this
  }

  def proxyHttpGet(paramName: String, path: String, overwriteProvided: Boolean = false): ProxiedInputModelBuilder = {
    val httpClient = new RestJsonClient(path)
    val signatureName = SignatureName(paramName)
    proxyParam(paramName, overwriteProvided) { modelInputParamMetadata =>
      val signature = modelInputParamMetadata.modelMetadata.signature
      signature.getInputValueType(signatureName) match {
        case Some(paramType) => httpGetAndDeserialize(paramType, httpClient)
        case None => Unit
      }
    }
  }

  def build(): ProxiedInputModel = new ProxiedInputModel(model, params.values)

  private def httpGetAndDeserialize(paramType: SignatureType, client: RestJsonClient): AnyRef = {
    val result = paramType.typingResult match {
      case t: TypingResult if t.canBeSubclassOf(Typed[Boolean]) => client.getJson[Boolean]().right.map(_.asInstanceOf[AnyRef])
      case t: TypingResult if t.canBeSubclassOf(Typed[Long]) => client.getJson[Long]().right.map(_.asInstanceOf[AnyRef])
      case t: TypingResult if t.canBeSubclassOf(Typed[Double]) => client.getJson[Double]().right.map(_.asInstanceOf[AnyRef])
      case t: TypingResult if t.canBeSubclassOf(Typed[String]) => client.getJson[String]().right.map(_.asInstanceOf[AnyRef])
      case _ => throw new IllegalArgumentException(s"Unsupported basic http get param supply type: ${paramType.typingResult}")
    }
    result.right.get
  }
}

object ProxiedInputModelBuilder {

  def apply(model: Model): ProxiedInputModelBuilder = new ProxiedInputModelBuilder(model)
}
