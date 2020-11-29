package pl.touk.nussknacker.prinz.util.http

import io.circe.{Decoder, Encoder}
import pl.touk.nussknacker.prinz.util.http.RestJsonClient.RestClientResponse
import sttp.client3.circe.asJson
import sttp.client3.{BodySerializer, HttpURLConnectionBackend, Identity, ResponseException, SttpBackend, SttpClientException, UriContext, basicRequest, circe}
import sttp.model.Uri

class RestJsonClient(val baseUrl: String, private val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()) {

  def postJsonBody[BODY, RESPONSE: Manifest](relativePath: String, body: BODY, params: RestRequestParams = EmptyRestRequestParams)
                                            (implicit encoder: Encoder[BODY], decoder: Decoder[RESPONSE]): RestClientResponse[RESPONSE] = {
    val request = basicRequest
      .post(uriFromRelativePath(relativePath, params.getParamsMap))
      .header("Content-Type", "application/json")
      .body(encoder(body).toString())
      .response(asJson[RESPONSE])
    wrapCaughtException(() => request.send(backend)
      .body.left.map(clientExceptionFromResponse)
    )
  }

  def getJson[RESPONSE: Manifest](relativePath: String, params: RestRequestParams = EmptyRestRequestParams)
                                 (implicit decoder: Decoder[RESPONSE]): RestClientResponse[RESPONSE] = {
    val request = basicRequest
      .get(uriFromRelativePath(relativePath, params.getParamsMap))
      .response(asJson[RESPONSE])
    wrapCaughtException(() => request.send(backend)
      .body.left.map(clientExceptionFromResponse)
    )
  }

  private def uriFromRelativePath(relativePath: String, params: Map[String, String]): Uri =
    uri"${s"$baseUrl$relativePath"}?$params"

  private def clientExceptionFromResponse(value: ResponseException[String, Exception]): RestClientException =
    new RestClientException(value.toString)

  private def wrapCaughtException[RESPONSE: Manifest](requestAction: () => Either[RestClientException, RESPONSE]): Either[RestClientException, RESPONSE] = try {
    requestAction()
  } catch {
    case e: SttpClientException => Left(new RestClientException(e.getMessage))
  }
}

object RestJsonClient {

  type RestClientResponse[RESPONSE] = Either[RestClientException, RESPONSE]
}
