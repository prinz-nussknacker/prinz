package pl.touk.nussknacker.prinz.util.http

import io.circe.{Decoder, Encoder}
import pl.touk.nussknacker.prinz.util.http.AbstractRestJsonClient.RestClientResponse
import sttp.client3.{HttpURLConnectionBackend, Identity, SttpBackend}

class RestJsonClient(baseUrl: String, private val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend())
  extends AbstractRestJsonClient(baseUrl) {

  def postJsonBody[BODY, RESPONSE: Manifest](body: BODY, relativePath: String = "", params: RestRequestParams = EmptyRestRequestParams)
                                            (implicit encoder: Encoder[BODY], decoder: Decoder[RESPONSE]): RestClientResponse[RESPONSE] = {
    val request = createPostJsonRequest(relativePath, body, params)
    wrapCaughtException(
      () => request.send(backend).body.left.map(clientExceptionFromResponse),
      e => Left(RestClientException(e.getMessage))
    )
  }

  def getJson[RESPONSE: Manifest](relativePath: String = "", params: RestRequestParams = EmptyRestRequestParams)
                                 (implicit decoder: Decoder[RESPONSE]): RestClientResponse[RESPONSE] = {
    val request = createGetRequest(relativePath, params)
    wrapCaughtException(
      () => request.send(backend).body.left.map(clientExceptionFromResponse),
      e => Left(RestClientException(e.getMessage))
    )
  }
}
