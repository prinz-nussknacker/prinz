package pl.touk.nussknacker.prinz.mlflow.repository

import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.api.{MLFRegisteredModel, MLFRegisteredModelName, MLFRegisteredModelVersion}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.{MLFRestRegisteredModel, MLFRestRegisteredModelVersion}
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.{MLFRestClient, MLFRestClientConfig}
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.util.time.Timestamp.instant

class MLFModelRepository(implicit val config: MLFConfig) extends ModelRepository {

  private val restClient = MLFRestClient(MLFRestClientConfig.fromMLFConfig(config))

  override def listModels: RepositoryResponse[List[MLFRegisteredModel]] =
    restClient.listModels()
      .left.map(new MLFRepositoryException(_))
      .right.map(_.map(toApi))

  private def toApi(model: MLFRestRegisteredModel): MLFRegisteredModel =
    MLFRegisteredModel(new MLFRegisteredModelName(model.name), instant(model.creation_timestamp),
      instant(model.last_updated_timestamp), model.latest_versions.map(toApi), this)

  private def toApi(ver: MLFRestRegisteredModelVersion): MLFRegisteredModelVersion =
    MLFRegisteredModelVersion(ver.name, ver.version, instant(ver.creation_timestamp),
      instant(ver.last_updated_timestamp), ver.current_stage, ver.source, ver.run_id, ver.status)
}
