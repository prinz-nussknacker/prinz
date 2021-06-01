package pl.touk.nussknacker.prinz.model.proxy.tranformer

import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.ModelInstance
import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}
import pl.touk.nussknacker.prinz.model.proxy.api.ProxiedInputModel

class TransformedModelInstance(originalModelInstance: ModelInstance,
                               proxiedModel: ProxiedInputModel,
                               paramProvider: TransformedParamProvider)
  extends ModelInstance(proxiedModel) {

  override protected def runVerified(inputMap: ModelInputData): ModelRunResult =
    paramProvider
      .transformInputData(inputMap)
      .flatMap { transformedInput =>
        originalModelInstance.run(transformedInput)
      }
}

trait ModelInputTransformer extends SignatureTransformer with TransformedParamProvider
