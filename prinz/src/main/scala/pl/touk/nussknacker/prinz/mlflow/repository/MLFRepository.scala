package pl.touk.nussknacker.prinz.mlflow.repository

import com.typesafe.scalalogging.Logger
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.api.{MLFRegisteredModel, MLFRegisteredModelName, MLFRegisteredModelVersion}
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.{MLFRestModelName, MLFRestRegisteredModel, MLFRestRegisteredModelVersion}
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFRestClient
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepository.toApi
import pl.touk.nussknacker.prinz.model.ModelName
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.util.time.Timestamp.instant

case class MLFRepository(private val config: MLFConfig) extends ModelRepository {

  private val restClient = MLFRestClient()

  override def listModels: RepositoryResponse[List[MLFRegisteredModel]] =
    restClient.listModels()
      .left.map(new MLFRepositoryException(_))
      .right.map(_.map(toApi))

  override def getModel(name: ModelName): RepositoryResponse[MLFRegisteredModel] =
    restClient.getModel(MLFRestModelName(name.internal))
      .left.map(new MLFRepositoryException(_))
      .right.map(toApi)
}

object MLFRepository {

  private def toApi(model: MLFRestRegisteredModel): MLFRegisteredModel =
    MLFRegisteredModel(MLFRegisteredModelName(model.name), instant(model.creation_timestamp),
      instant(model.last_updated_timestamp), model.latest_versions.map(toApi))

  private def toApi(ver: MLFRestRegisteredModelVersion): MLFRegisteredModelVersion =
    MLFRegisteredModelVersion(ver.name, ver.version, instant(ver.creation_timestamp),
      instant(ver.last_updated_timestamp), ver.current_stage, ver.source, ver.run_id, ver.status)
}
