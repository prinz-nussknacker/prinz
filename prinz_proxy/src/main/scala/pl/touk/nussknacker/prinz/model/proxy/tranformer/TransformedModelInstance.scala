package pl.touk.nussknacker.prinz.model.proxy.tranformer

import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelSignature, SignatureNotFoundException}
import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}
import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult

class TransformedModelInstance(originalModelInstance: ModelInstance,
                               transformedSignatureProvider: TransformedSignatureProvider,
                               paramProvider: TransformedParamProvider)
  extends ModelInstance(originalModelInstance.model) {

  private val transformedSignatureOption: ProvideSignatureResult =
    transformedSignatureProvider
      .provideSignature(originalModelInstance.model)

  override def run(inputMap: ModelInputData): ModelRunResult =
    paramProvider
      .transformInputData(inputMap)
      .flatMap { transformedInput =>
        originalModelInstance.run(transformedInput)
      }

  override def getParameterDefinition: ModelSignature = transformedSignatureOption match {
    case Some(value) => value
    case None => throw SignatureNotFoundException(originalModelInstance.model)
  }
}

trait ModelInputTransformer extends SignatureTransformer with TransformedParamProvider
