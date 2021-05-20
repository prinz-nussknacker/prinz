package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelMetadata, ModelName, ModelSignatureLocationMetadata, ModelVersion}
import pl.touk.nussknacker.prinz.model.proxy.composite.{ProxiedInputModelInstance, ProxiedModelCompositeInputParam, ProxiedModelInputParam}
import pl.touk.nussknacker.prinz.model.proxy.tranformer.{ModelInputTransformer, SignatureTransformer, TransformedModelInstance,
  TransformedParamProvider, TransformedSignatureProvider}


class ProxiedInputModel private(model: Model,
                                modelName: ProxiedInputModelName,
                                transformedSignatureProvider: TransformedSignatureProvider,
                                proxySupplier: (ModelMetadata, ModelInstance) => ModelInstance)
  extends Model {

  override protected val signatureOption: ProvideSignatureResult = transformedSignatureProvider
    .provideSignature(ProxiedModelSignatureLocationMetadata(model))

  private val originalModelInstance: ModelInstance = model.toModelInstance

  private val proxiedName: ModelName = modelName

  private val proxiedModelInstance: ModelInstance = proxySupplier(model.getMetadata, originalModelInstance)

  def this(model: Model,
           proxiedParams: Iterable[ProxiedModelInputParam],
           compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]]) {
    this(
      model,
      CompositeProxiedInputModelName(model),
      new TransformedSignatureProvider(identity),
      (metadata, instance) => new ProxiedInputModelInstance(metadata, instance, proxiedParams, compositeProxiedParams)
    )
  }

  def this(model: Model,
           signatureTransformer: SignatureTransformer,
           paramProvider: TransformedParamProvider) {
    this(
      model,
      TransformedProxiedInputModelName(model),
      new TransformedSignatureProvider(signatureTransformer),
      (_, instance) => new TransformedModelInstance(
        instance,
        paramProvider)
    )
  }

  def this(model: Model,
           transformer: ModelInputTransformer) {
    this(
      model,
      TransformedProxiedInputModelName(model),
      new TransformedSignatureProvider(transformer),
      (_, instance) => new TransformedModelInstance(
        instance,
        transformer)
    )
  }

  override def toModelInstance: ModelInstance = proxiedModelInstance

  override protected def getName: ModelName = proxiedName

  override protected def getVersion: ModelVersion = model.getMetadata.modelVersion
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

final case class ProxiedModelSignatureLocationMetadata(proxiedModel: Model) extends ModelSignatureLocationMetadata
