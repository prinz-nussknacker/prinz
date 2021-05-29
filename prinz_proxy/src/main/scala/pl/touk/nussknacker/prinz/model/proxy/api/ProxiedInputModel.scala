package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.proxy.api.ProxiedInputModel.filtered
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelMetadata, ModelName,
  ModelSignatureLocationMetadata, ModelVersion, SignatureName}
import pl.touk.nussknacker.prinz.model.proxy.composite.{ProxiedInputModelInstance,
  ProxiedModelCompositeInputParam, ProxiedModelInputParam}
import pl.touk.nussknacker.prinz.model.proxy.tranformer.{FilteredSignatureTransformer,
  ModelInputTransformer, SignatureTransformer, TransformedModelInstance,
  TransformedParamProvider, TransformedSignatureProvider}


class ProxiedInputModel private(originalModel: Model,
                                modelName: ProxiedInputModelName,
                                transformedSignatureProvider: TransformedSignatureProvider,
                                proxySupplier: (ModelMetadata, ModelInstance, ProxiedInputModel) => ModelInstance)
  extends Model {

  def this(proxiedModel: Model,
           proxiedParams: Iterable[ProxiedModelInputParam],
           compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]]) {
    this(
      proxiedModel,
      CompositeProxiedInputModelName(proxiedModel),
      new TransformedSignatureProvider(filtered(proxiedParams, compositeProxiedParams)),
      (metadata, instance, proxiedModel) => new ProxiedInputModelInstance(
        metadata,
        instance,
        proxiedModel,
        proxiedParams,
        compositeProxiedParams)
    )
  }

  def this(proxiedModel: Model,
           signatureTransformer: SignatureTransformer,
           paramProvider: TransformedParamProvider) {
    this(
      proxiedModel,
      TransformedProxiedInputModelName(proxiedModel),
      new TransformedSignatureProvider(signatureTransformer),
      (_, instance, proxiedModel) => new TransformedModelInstance(
        instance,
        proxiedModel,
        paramProvider)
    )
  }

  def this(proxiedModel: Model,
           signatureTransformer: ModelInputTransformer) {
    this(
      proxiedModel,
      TransformedProxiedInputModelName(proxiedModel),
      new TransformedSignatureProvider(signatureTransformer),
      (_, instance, proxiedModel) => new TransformedModelInstance(
        instance,
        proxiedModel,
        signatureTransformer)
    )
  }

  override def toModelInstance: ModelInstance = {
    val originalModelInstance = originalModel.toModelInstance
    proxySupplier(originalModel.getMetadata, originalModelInstance, this)
  }

  override protected val signatureOption: ProvideSignatureResult = {
    val metadata = ProxiedModelSignatureLocationMetadata(originalModel)
    transformedSignatureProvider.provideSignature(metadata)
  }

  override protected val name: ModelName = modelName

  override protected val version: ModelVersion = originalModel.getMetadata.modelVersion
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

  private def collectRemovedParams(proxiedParams: Iterable[ProxiedModelInputParam],
                                  compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]]): Iterable[SignatureName] = {
    val proxiedNames = proxiedParams.map(_.paramName)
    val composedProxiedNames = compositeProxiedParams.flatMap(_.proxiedParams)
    proxiedNames ++ composedProxiedNames
  }

  private def filtered(proxiedParams: Iterable[ProxiedModelInputParam],
                                   compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]]): FilteredSignatureTransformer =
    new FilteredSignatureTransformer(collectRemovedParams(proxiedParams, compositeProxiedParams))
}

final case class ProxiedModelSignatureLocationMetadata(proxiedModel: Model) extends ModelSignatureLocationMetadata
