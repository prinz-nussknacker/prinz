package pl.touk.nussknacker.prinz.util.http

import io.circe
import io.circe.{Decoder, Encoder}
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import sttp.client3.circe.asJson
import sttp.client3.{Identity, RequestT, ResponseException, SttpBackend, SttpClientException, UriContext, basicRequest}
import sttp.model.Uri

import scala.concurrent.Future

abstract class AbstractRestJsonClient(private val baseUrl: String) {

  protected def createPostJsonRequest[BODY, RESPONSE: Manifest](relativePath: String, body: BODY, params: RestRequestParams)
                                                               (implicit encoder: Encoder[BODY], decoder: Decoder[RESPONSE]):
  RequestT[Identity, Either[ResponseException[String, circe.Error], RESPONSE], Any] =
    basicRequest
      .post(uriFromRelativePath(relativePath, params.getParamsMap))
      .header("Content-Type", "application/json")
      .body(encoder(body).noSpaces)
      .response(asJson[RESPONSE])

  protected def createGetRequest[BODY, RESPONSE: Manifest](relativePath: String, params: RestRequestParams)
                                                          (implicit decoder: Decoder[RESPONSE]):
  RequestT[Identity, Either[ResponseException[String, circe.Error], RESPONSE], Any] =
    basicRequest
      .get(uriFromRelativePath(relativePath, params.getParamsMap))
      .response(asJson[RESPONSE])

  private def uriFromRelativePath(relativePath: String, params: Map[String, String]): Uri =
    uri"${s"$baseUrl$relativePath"}?$params"

  protected def clientExceptionFromResponse(value: ResponseException[String, Exception]): RestClientException =
    RestClientException(value)

  protected def wrapCaughtException[R](requestAction: () => R, exceptionHandler: Exception => R): R = try {
    requestAction()
  } catch {
    case e: SttpClientException => exceptionHandler(e)
  }

  /*protected def defaultWrapCaughtException[RESPONSE: Manifest, F[_]]
  (request: RequestT[Identity, Either[ResponseException[String, circe.Error], RESPONSE], Any], backend: SttpBackend[F, Any]) = wrapCaughtException(
    () => request.send(backend).map { response => response.body.left.map(clientExceptionFromResponse) },
    e => Future(Left(RestClientException(e)))
  )*/
}

object AbstractRestJsonClient {

  type RestClientResponse[RESPONSE] = Either[RestClientException, RESPONSE]
}
