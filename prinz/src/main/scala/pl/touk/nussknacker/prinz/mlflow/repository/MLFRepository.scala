package pl.touk.nussknacker.prinz.mlflow.repository

import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.model.api.{MLFRegisteredModel, MLFRegisteredModelName, MLFRegisteredModelVersion}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.{RestMLFModelName, RestRegisteredModel, RestRegisteredModelVersion}
import pl.touk.nussknacker.prinz.mlflow.model.rest.client
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFRestClient
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepository.toApi
import pl.touk.nussknacker.prinz.model.{Model, ModelName}
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.util.time.Timestamp.instant

case class MLFRepository(hostUrl: URL) extends ModelRepository {

  private val restClient = client.MLFRestClient(hostUrl)

  override def listModels: RepositoryResponse[List[Model]] =
    restClient.listModels()
      .left.map(new MLFRepositoryException(_))
      .right.map(_.map(toApi))

  override def getModel(name: ModelName): RepositoryResponse[Model] =
    restClient.getModel(RestMLFModelName(name.internal))
      .left.map(new MLFRepositoryException(_))
      .right.map(toApi)
}

object MLFRepository {

  private def toApi(model: RestRegisteredModel): MLFRegisteredModel =
    MLFRegisteredModel(MLFRegisteredModelName(model.name), instant(model.creation_timestamp),
      instant(model.last_updated_timestamp), model.latest_versions.map(toApi))

  private def toApi(ver: RestRegisteredModelVersion): MLFRegisteredModelVersion =
    MLFRegisteredModelVersion(ver.name, ver.version, instant(ver.creation_timestamp),
      instant(ver.last_updated_timestamp), ver.current_stage, ver.source, ver.run_id, ver.status)
}
