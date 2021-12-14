package pl.touk.nussknacker.prinz.container

import pl.touk.nussknacker.prinz.model.repository.ModelRepository
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelSignature}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.duration.FiniteDuration

trait TestModelsManager {

  def integrationName: String

  def awaitTimeout: FiniteDuration

  def getRepository: ModelRepository

  def getModel(extract: List[Model] => Model = getElasticnetWineModelModel(1)): Option[Model] = {
    val repository = getRepository
    repository.listModels
      .toOption
      .map(extract)
  }

  def getModelInstance(extract: List[Model] => Model = getElasticnetWineModelModel(1)): Option[ModelInstance] = {
    val model = getModel(extract)
    model.map(_.toModelInstance)
  }

  def getFraudDetectionModel(models: List[Model]): Model =
    models.filter(_.getMetadata.modelName.toString.contains("FraudDetection")).head

  def getElasticnetWineModelModel(modelId: Int)(models: List[Model]): Model =
    models.filter(_.getMetadata.modelName.toString.contains("ElasticnetWineModel-" + modelId)).head

  def constructInputMap(value: Any, signature: ModelSignature): VectorMultimap[String, Any] = {
    val names = signature.getSignatureInputs.map(_.signatureName.name)
    val data = List.fill(names.length)(value)
    VectorMultimap(names.zip(data))
  }
}
