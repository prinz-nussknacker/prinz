package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.model.{Model, SignatureName, SignatureType}
import pl.touk.nussknacker.prinz.util.http.AsyncRestJsonClient

import scala.concurrent.Future

class ProxiedHttpInputModelBuilder(model: Model) extends ProxiedInputModelBuilder(model) {

  def proxyHttpGet(paramName: String, path: String): this.type = {
    val httpClient = new AsyncRestJsonClient(path)
    val signatureName = SignatureName(paramName)
    proxyParam(paramName) { modelInputParamMetadata =>
      val signature = modelInputParamMetadata.modelMetadata.signature
      signature.getInputValueType(signatureName) match {
        case Some(paramType) => httpGetAndDeserialize(paramType, httpClient)
        case None => Future(Unit)
      }
    }
  }

  private def httpGetAndDeserialize(paramType: SignatureType, client: AsyncRestJsonClient): Future[AnyRef] = {
    val result = paramType.typingResult match {
      case t: TypingResult if t.canBeSubclassOf(Typed[String]) => client.getJson[String]()
      case t: TypingResult if t.canBeSubclassOf(Typed[Double]) => client.getJson[Double]()
      case t: TypingResult if t.canBeSubclassOf(Typed[Long]) => client.getJson[Long]()
      case t: TypingResult if t.canBeSubclassOf(Typed[Boolean]) => client.getJson[Boolean]()
      case _ => throw new IllegalArgumentException(s"Unsupported basic http get param supply type: ${paramType.typingResult}")
    }
    result.map(_.right.get.asInstanceOf[AnyRef])
  }
}
