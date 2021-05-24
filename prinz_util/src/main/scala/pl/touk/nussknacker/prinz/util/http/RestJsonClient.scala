package pl.touk.nussknacker.prinz.util.http

import io.circe
import io.circe.{Decoder, Encoder}
import pl.touk.nussknacker.prinz.util.http.AbstractRestJsonClient.RestClientResponse
import sttp.client3.{HttpURLConnectionBackend, Identity, RequestT, ResponseException, SttpBackend}

class RestJsonClient(baseUrl: String, private val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend())
  extends AbstractRestJsonClient(baseUrl) {

  def postJsonBody[BODY, RESPONSE: Manifest](body: BODY, relativePath: String = "", params: RestRequestParams = EmptyRestRequestParams)
                                            (implicit encoder: Encoder[BODY], decoder: Decoder[RESPONSE]): RestClientResponse[RESPONSE] = {
    val request = createPostJsonRequest(relativePath, body, params)
    defaultWrapCaughtException(request)
  }

  def getJson[RESPONSE: Manifest](relativePath: String = "", params: RestRequestParams = EmptyRestRequestParams)
                                 (implicit decoder: Decoder[RESPONSE]): RestClientResponse[RESPONSE] = {
    val request = createGetRequest(relativePath, params)
    defaultWrapCaughtException(request)
  }

  private def defaultWrapCaughtException[RESPONSE: Manifest]
  (request: RequestT[Identity, Either[ResponseException[String, circe.Error], RESPONSE], Any]) = wrapCaughtException(
    () => request.send(backend).body.left.map(clientExceptionFromResponse),
    e => Left(RestClientException(e))
  )
}
