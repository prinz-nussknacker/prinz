package pl.touk.nussknacker.prinz.util.http

import org.json4s.{DefaultFormats, Formats, JNothing, Serialization}
import org.json4s.native.Serialization
import sttp.client3.{Empty, HttpURLConnectionBackend, Identity, RequestT, ResponseException, SttpBackend, SttpClientException, UriContext, basicRequest}
import sttp.client3.json4s.SttpJson4sApi
import sttp.model.Uri

//TODO: Parse object to JSON in body?
class RestJsonClient(val baseUrl: String, private val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()) extends SttpJson4sApi {

  private implicit val SERIALIZATION: Serialization = Serialization
  private implicit val FORMATS: Formats = DefaultFormats

  def postJsonBody[RESPONSE: Manifest](relativePath: String, body: String): Either[RestClientException, RESPONSE] = {
    val request = basicRequest
      .post(uriFromRelativePath(relativePath))
      .contentType("application/json")
      .body(body)
      .response(asJson[RESPONSE])
    wrapCaughtException(() => request.send(backend)
      .body.left.map(clientExceptionFromResponse)
    )
  }

  def getJson[RESPONSE: Manifest](relativePath: String): Either[RestClientException, RESPONSE] = {
    val request = basicRequest
      .get(uriFromRelativePath(relativePath))
      .response(asJson[RESPONSE])
    wrapCaughtException(() => request.send(backend)
      .body.left.map(clientExceptionFromResponse)
    )
  }

  def getJsonBody[RESPONSE: Manifest](relativePath: String, body: String): Either[RestClientException, RESPONSE] = {
    val request = basicRequest
      .get(uriFromRelativePath(relativePath))
      .contentType("application/json")
      .body(body)
      .response(asJson[RESPONSE])
    wrapCaughtException(() => request.send(backend)
      .body.left.map(clientExceptionFromResponse)
    )
  }

  private def uriFromRelativePath(relativePath: String): Uri = uri"$baseUrl$relativePath"

  private def clientExceptionFromResponse(value: ResponseException[String, Exception]): RestClientException =
    RestClientException(value.toString)

  private def wrapCaughtException[RESPONSE: Manifest](requestAction: () => Either[RestClientException, RESPONSE]): Either[RestClientException, RESPONSE] = try {
    requestAction()
  } catch {
    case e: SttpClientException => Left(RestClientException(e.getMessage))
  }
}
