package pl.touk.nussknacker.prinz.util.http

import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats, Serialization}
import pl.touk.nussknacker.prinz.model.repository.ModelRepositoryException
import sttp.client3.json4s.SttpJson4sApi
import sttp.client3.{HttpURLConnectionBackend, Identity, ResponseException, SttpBackend, SttpClientException, UriContext, basicRequest}
import sttp.model.{Methods, Uri}

class RestJsonClient(val baseUrl: String, private val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()) extends SttpJson4sApi with Methods {

  private implicit val SERIALIZATION: Serialization = Serialization
  private implicit val FORMATS: Formats = DefaultFormats

  def postJsonBody[BODY <: AnyRef, RESPONSE: Manifest]
  (relativePath: String, body: BODY): Either[RestClientException, RESPONSE] = {
    val request = basicRequest
      .post(uriFromRelativePath(relativePath, EmptyRequestParams.getParamsMap))
      .header("Content-Type", "application/json")
      .body(body)
      .response(asJson[RESPONSE])
    wrapCaughtException(() => request.send(backend)
      .body.left.map(clientExceptionFromResponse)
    )
  }

  def getJson[RESPONSE: Manifest](relativePath: String): Either[RestClientException, RESPONSE] =
    getJson[Any, RESPONSE](relativePath, EmptyRequestParams)

  def getJson[PARAMS, RESPONSE: Manifest](relativePath: String, params: RequestParams[PARAMS]): Either[RestClientException, RESPONSE] = {
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

  def apply(baseUrl: String, backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()): RestJsonClient =
    new RestJsonClient(baseUrl, backend)
}
