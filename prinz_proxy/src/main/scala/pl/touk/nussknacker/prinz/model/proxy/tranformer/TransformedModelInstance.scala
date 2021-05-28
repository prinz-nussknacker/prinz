package pl.touk.nussknacker.prinz.model.proxy.tranformer

import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.ModelInstance
import pl.touk.nussknacker.prinz.model.ModelInstance.{ModelInputData, ModelRunResult}

class TransformedModelInstance(originalModelInstance: ModelInstance,
                               paramProvider: TransformedParamProvider)
  extends ModelInstance(originalModelInstance.model) {

  override def runVerified(inputMap: ModelInputData): ModelRunResult =
    paramProvider
      .transformInputData(inputMap)
      .flatMap { transformedInput =>
        originalModelInstance.run(transformedInput)
      }
}

trait ModelInputTransformer extends SignatureTransformer with TransformedParamProvider
