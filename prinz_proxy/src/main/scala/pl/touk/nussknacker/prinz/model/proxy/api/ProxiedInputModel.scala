package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelMetadata, ModelName, ModelVersion}
import pl.touk.nussknacker.prinz.model.proxy.composite.{ProxiedInputModelInstance, ProxiedModelCompositeInputParam, ProxiedModelInputParam}
import pl.touk.nussknacker.prinz.model.proxy.tranformer.{ModelInputTransformer,
  SignatureTransformer, TransformedModelInstance,
  TransformedParamProvider, TransformedSignatureProvider}


class ProxiedInputModel private(model: Model,
                                modelName: ProxiedInputModelName,
                                proxySupplier: (ModelMetadata, ModelInstance) => ModelInstance)
  extends Model {

  private val originalModelInstance: ModelInstance = model.toModelInstance

  private val proxiedName: ModelName = modelName

  private val modelMetadata: ModelMetadata = ModelMetadata(model.getName, model.getVersion, originalModelInstance.getSignature)

  private val proxiedModelInstance: ModelInstance = proxySupplier(modelMetadata, originalModelInstance)

  def this(model: Model,
           proxiedParams: Iterable[ProxiedModelInputParam],
           compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]]) {
    this(
      model,
      CompositeProxiedInputModelName(model),
      (metadata, instance) => new ProxiedInputModelInstance(metadata, instance, proxiedParams, compositeProxiedParams)
    )
  }

  def this(model: Model,
           signatureTransformer: SignatureTransformer,
           paramProvider: TransformedParamProvider) {
    this(
      model,
      TransformedProxiedInputModelName(model),
      (_, instance) => new TransformedModelInstance(
        instance,
        new TransformedSignatureProvider(signatureTransformer),
        paramProvider)
    )
  }

  def this(model: Model,
           transformer: ModelInputTransformer) {
    this(
      model,
      TransformedProxiedInputModelName(model),
      (_, instance) => new TransformedModelInstance(
        instance,
        new TransformedSignatureProvider(transformer),
        transformer)
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

  def apply(model: Model, transformer: ModelInputTransformer): ProxiedInputModel =
    new ProxiedInputModel(model, transformer)
}
