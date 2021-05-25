package pl.touk.nussknacker.prinz.util.http

import com.typesafe.scalalogging.LazyLogging
import io.circe
import io.circe.{Decoder, Encoder}
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.util.http.AbstractRestJsonClient.RestClientResponse
import sttp.client3.{Identity, RequestT, ResponseException, SttpBackend}
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future

class AsyncRestJsonClient(baseUrl: String, private val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend())
  extends AbstractRestJsonClient(baseUrl) with LazyLogging {

  def postJsonBody[BODY, RESPONSE: Manifest](body: BODY, relativePath: String = "", params: RestRequestParams = EmptyRestRequestParams)
                                            (implicit encoder: Encoder[BODY], decoder: Decoder[RESPONSE]): Future[RestClientResponse[RESPONSE]] = {
    val request = createPostJsonRequest(relativePath, body, params)
    wrapFutureResponseException(request)
  }

  def getJson[RESPONSE: Manifest](relativePath: String = "", params: RestRequestParams = EmptyRestRequestParams)
                                 (implicit decoder: Decoder[RESPONSE]): Future[RestClientResponse[RESPONSE]] = {
    val request = createGetRequest(relativePath, params)
    wrapFutureResponseException(request)
  }

  private def wrapFutureResponseException[RESPONSE: Manifest]
    (request: RequestT[Identity, Either[ResponseException[String, circe.Error], RESPONSE], Any]) = wrapCaughtException(
    () => request.send(backend).map { response => response.body.left.map(clientExceptionFromResponse) },
    e => Future(Left(RestClientException(e)))
  )
}
