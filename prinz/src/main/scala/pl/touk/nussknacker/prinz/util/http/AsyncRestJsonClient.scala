package pl.touk.nussknacker.prinz.util.http

import com.typesafe.scalalogging.LazyLogging
import io.circe.{Decoder, Encoder}
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.util.http.AbstractRestJsonClient.RestClientResponse
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.future.AsyncHttpClientFutureBackend

import scala.concurrent.Future

class AsyncRestJsonClient(baseUrl: String, private val backend: SttpBackend[Future, Any] = AsyncHttpClientFutureBackend())
  extends AbstractRestJsonClient(baseUrl) with LazyLogging {

  def postJsonBody[BODY, RESPONSE: Manifest](relativePath: String, body: BODY, params: RestRequestParams = EmptyRestRequestParams)
                                            (implicit encoder: Encoder[BODY], decoder: Decoder[RESPONSE]): Future[RestClientResponse[RESPONSE]] = {
    val request = createPostJsonRequest(relativePath, body, params)
    logger.info("Sending request in {}: {}", getClass.getSimpleName, request)
    wrapCaughtException(
      () => request.send(backend).map { response => response.body.left.map(clientExceptionFromResponse) },
      exception => Future(Left(RestClientException(exception.getMessage)))
    )
  }
}
