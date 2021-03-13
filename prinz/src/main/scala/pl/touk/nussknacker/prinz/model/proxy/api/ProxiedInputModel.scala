package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelMetadata, ModelName, ModelVersion}


class ProxiedInputModel(model: Model,
                        proxiedParams: Iterable[ProxiedModelInputParam],
                        compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]]) extends Model {

  private val originalModelInstance = model.toModelInstance

  private val proxiedName = ProxyInputModelName(model.getName)

  private val modelMetadata = ModelMetadata(model.getName, model.getVersion, originalModelInstance.getSignature)

  private val proxiedModelInstance =
    new ProxiedInputModelInstance(modelMetadata, originalModelInstance, proxiedParams, compositeProxiedParams)

  override def getName: ModelName = proxiedName

  override def getVersion: ModelVersion = model.getVersion

  override def toModelInstance: ModelInstance = proxiedModelInstance
}
