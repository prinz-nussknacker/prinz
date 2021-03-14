package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelMetadata, ModelName, ModelVersion}
import pl.touk.nussknacker.prinz.model.proxy.composite.{ProxiedInputModelInstance, ProxiedModelCompositeInputParam, ProxiedModelInputParam}
import pl.touk.nussknacker.prinz.model.proxy.tranformer.{SignatureTransformer, TransformedModelInstance, TransformedParamProvider}


class ProxiedInputModel private(model: Model, modelName: ProxiedInputModelName, proxy: ModelInstance)
  extends Model {

  private val originalModelInstance: ModelInstance = model.toModelInstance

  private val proxiedModelInstance: ModelInstance = proxy

  private val proxiedName: ModelName = modelName

  private val modelMetadata: ModelMetadata = ModelMetadata(model.getName, model.getVersion, originalModelInstance.getSignature)

  def this(model: Model,
           proxiedParams: Iterable[ProxiedModelInputParam],
           compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]]) {
    this(
      model,
      CompositeProxiedInputModelName(model),
      new ProxiedInputModelInstance(modelMetadata, originalModelInstance, proxiedParams, compositeProxiedParams)
    )
  }

  def this(model: Model,
           signatureTransformer: SignatureTransformer,
           paramProvider: TransformedParamProvider) {
    this(
      model,
      TransformedProxiedInputModelName(model),
      new TransformedModelInstance(originalModelInstance, signatureTransformer, paramProvider)
    )
  }

  override def getName: ModelName = proxiedName

  override def getVersion: ModelVersion = model.getVersion

  override def toModelInstance: ModelInstance = proxiedModelInstance
}

object ProxiedInputModel {

  def apply(model: Model,
            proxiedParams: Iterable[ProxiedModelInputParam],
            compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]]): ProxiedInputModel =
    new ProxiedInputModel(model, proxiedParams, compositeProxiedParams)

  def apply(model: Model,
            signatureTransformer: SignatureTransformer,
            paramProvider: TransformedParamProvider): ProxiedInputModel =
    new ProxiedInputModel(model, signatureTransformer, paramProvider)
}
