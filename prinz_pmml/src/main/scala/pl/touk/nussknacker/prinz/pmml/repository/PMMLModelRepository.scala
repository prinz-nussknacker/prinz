package pl.touk.nussknacker.prinz.pmml.repository

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.model.ModelName
import pl.touk.nussknacker.prinz.pmml.PMMLConfig
import pl.touk.nussknacker.prinz.pmml.model.PMMLModel
import pl.touk.nussknacker.prinz.util.repository.client.{RepositoryClient, RepositoryClientFactory}
import pl.touk.nussknacker.prinz.util.repository.payload.ModelPayload

class PMMLModelRepository(implicit val config: PMMLConfig)
  extends ModelRepository with RepositoryClient {

  private val uri = config.modelsDirectory

  override def listModels: RepositoryResponse[List[PMMLModel]] =
    client.listModelFiles(uri).right.map(it => it.map(p => PMMLModel(mapPayload(p))).toList)

  override def getModel(name: ModelName): RepositoryResponse[PMMLModel] =
    client.listModelFiles(uri)
      .right.map(it => it.map(mapPayload).filter(p => p.name == name.toString))
      .map(it => PMMLModel(it.head))

  private def mapPayload(payload: ModelPayload): PMMLModelPayload =
    PMMLModelPayload(payload, client.openModelFile(payload.path), config.fileExtension, config.modelVersionSeparator)
}
