package pl.touk.nussknacker.prinz.mlflow.model.rest.api

import pl.touk.nussknacker.prinz.util.http.RestRequestParams

case class GetAllRegisteredModelsRequest(max_results: Int, page_token: Int) extends RestRequestParams

case class GetRegisteredModelRequest(name: String) extends RestRequestParams