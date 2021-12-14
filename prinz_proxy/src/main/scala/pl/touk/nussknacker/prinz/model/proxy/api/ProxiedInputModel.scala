package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.proxy.api.ProxiedInputModel.filteredTransform
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
                                proxySupplier: (Model, ProxiedInputModel) => ModelInstance)
  extends Model {

  def this(model: Model,
           proxiedParams: Iterable[ProxiedModelInputParam],
           compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: Any]]) {
    this(
      model,
      CompositeProxiedInputModelName(model),
      new TransformedSignatureProvider(filteredTransform(proxiedParams, compositeProxiedParams)),
      (originalModel, proxiedInputModel) => new ProxiedInputModelInstance(
        originalModel.getMetadata,
        originalModel.toModelInstance,
        proxiedInputModel,
        proxiedParams,
        compositeProxiedParams)
    )
  }

  def this(model: Model,
           signatureTransformer: SignatureTransformer,
           paramProvider: TransformedParamProvider) {
    this(
      model,
      TransformedProxiedInputModelName(model),
      new TransformedSignatureProvider(signatureTransformer),
      (originalModel, proxiedInputModel) => new TransformedModelInstance(
        originalModel.toModelInstance,
        proxiedInputModel,
        paramProvider)
    )
  }

  def this(proxiedModel: Model,
           signatureTransformer: ModelInputTransformer) {
    this(
      proxiedModel,
      TransformedProxiedInputModelName(proxiedModel),
      new TransformedSignatureProvider(signatureTransformer),
      (originalModel, proxiedInputModel) => new TransformedModelInstance(
        originalModel.toModelInstance,
        proxiedInputModel,
        signatureTransformer)
    )
  }

  override def toModelInstance: ModelInstance = proxySupplier(originalModel, this)

  override protected val signatureOption: ProvideSignatureResult = {
    val metadata = ProxiedModelSignatureLocationMetadata(originalModel)
    transformedSignatureProvider.provideSignature(metadata)
  }

  override protected val name: ModelName = modelName

  override protected val version: ModelVersion = originalModel.getMetadata.modelVersion
}

object ProxiedInputModel {

  def apply(model: Model,
            proxiedParams: Iterable[ProxiedModelInputParam],
            compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: Any]]): ProxiedInputModel =
    new ProxiedInputModel(model, proxiedParams, compositeProxiedParams)

  def apply(model: Model,
            signatureTransformer: SignatureTransformer,
            paramProvider: TransformedParamProvider): ProxiedInputModel =
    new ProxiedInputModel(model, signatureTransformer, paramProvider)

  def apply(model: Model,
            transformer: ModelInputTransformer): ProxiedInputModel =
    new ProxiedInputModel(model, transformer)

  private def collectRemovedParams(proxiedParams: Iterable[ProxiedModelInputParam],
                                  compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: Any]]): Iterable[SignatureName] = {
    val proxiedNames = proxiedParams.map(_.paramName)
    val composedProxiedNames = compositeProxiedParams.flatMap(_.proxiedParams)
    proxiedNames ++ composedProxiedNames
  }

  private def filteredTransform(proxiedParams: Iterable[ProxiedModelInputParam],
                                compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: Any]]
                               ): FilteredSignatureTransformer =
    new FilteredSignatureTransformer(collectRemovedParams(proxiedParams, compositeProxiedParams))
}

final case class ProxiedModelSignatureLocationMetadata(proxiedModel: Model) extends ModelSignatureLocationMetadata
