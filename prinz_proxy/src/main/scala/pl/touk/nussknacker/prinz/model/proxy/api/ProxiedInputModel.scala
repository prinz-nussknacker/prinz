package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelMetadata, ModelName, ModelSignatureLocationMetadata, ModelVersion}
import pl.touk.nussknacker.prinz.model.proxy.composite.{ProxiedInputModelInstance, ProxiedModelCompositeInputParam, ProxiedModelInputParam}
import pl.touk.nussknacker.prinz.model.proxy.tranformer.{FilteredSignatureTransformer, ModelInputTransformer,
  SignatureTransformer, TransformedModelInstance, TransformedParamProvider, TransformedSignatureProvider}


class ProxiedInputModel private(proxiedModel: Model,
                                modelName: ProxiedInputModelName,
                                transformedSignatureProvider: TransformedSignatureProvider,
                                proxySupplier: (ModelMetadata, ModelInstance) => ModelInstance)
  extends Model {

  def this(proxiedModel: Model,
           proxiedParams: Iterable[ProxiedModelInputParam],
           compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]]) {
    this(
      proxiedModel,
      CompositeProxiedInputModelName(proxiedModel),
      new TransformedSignatureProvider(new FilteredSignatureTransformer(proxiedParams)),
      (metadata, instance) => new ProxiedInputModelInstance(metadata, instance, proxiedParams, compositeProxiedParams)
    )
  }

  def this(proxiedModel: Model,
           signatureTransformer: SignatureTransformer,
           paramProvider: TransformedParamProvider) {
    this(
      proxiedModel,
      TransformedProxiedInputModelName(proxiedModel),
      new TransformedSignatureProvider(signatureTransformer),
      (_, instance) => new TransformedModelInstance(
        instance,
        paramProvider)
    )
  }

  def this(proxiedModel: Model,
           transformer: ModelInputTransformer) {
    this(
      proxiedModel,
      TransformedProxiedInputModelName(proxiedModel),
      new TransformedSignatureProvider(transformer),
      (_, instance) => new TransformedModelInstance(
        instance,
        transformer)
    )
  }

  override def toModelInstance: ModelInstance = {
    val originalModelInstance = proxiedModel.toModelInstance
    proxySupplier(proxiedModel.getMetadata, originalModelInstance)
  }

  override protected val signatureOption: ProvideSignatureResult = {
    val metadata = ProxiedModelSignatureLocationMetadata(proxiedModel)
    transformedSignatureProvider.provideSignature(metadata)
  }

  override protected val name: ModelName = modelName

  override protected val version: ModelVersion = proxiedModel.getMetadata.modelVersion
}

object ProxiedInputModel {

  def apply(proxiedModel: Model,
            proxiedParams: Iterable[ProxiedModelInputParam],
            compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]]): ProxiedInputModel =
    new ProxiedInputModel(proxiedModel, proxiedParams, compositeProxiedParams)

  def apply(proxiedModel: Model,
            signatureTransformer: SignatureTransformer,
            paramProvider: TransformedParamProvider): ProxiedInputModel =
    new ProxiedInputModel(proxiedModel, signatureTransformer, paramProvider)

  def apply(proxiedModel: Model, transformer: ModelInputTransformer): ProxiedInputModel =
    new ProxiedInputModel(proxiedModel, transformer)
}

final case class ProxiedModelSignatureLocationMetadata(proxiedModel: Model) extends ModelSignatureLocationMetadata
