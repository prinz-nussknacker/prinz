package pl.touk.nussknacker.prinz.model.proxy.composite

import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.model.ModelInstance.ModelRunResult
import pl.touk.nussknacker.prinz.model.proxy.api.ProxiedInputModel
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelMetadata}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.Future

class ProxiedInputModelInstance(originalModelMetadata: ModelMetadata,
                                originalModelInstance: ModelInstance,
                                proxiedModel: ProxiedInputModel,
                                proxiedParams: Iterable[ProxiedModelInputParam],
                                compositeProxiedParams: Iterable[ProxiedModelCompositeInputParam[_ <: AnyRef]])
  extends ModelInstance(proxiedModel) {

  override protected def runVerified(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult = {
    val addInputParams = supplyNonProvidedInputs(inputMap)
    val addComposedParams = addInputParams.flatMap(supplyNonProvidedComposedInputs)
    addComposedParams.flatMap(originalModelInstance.run)
  }

  private def supplyNonProvidedInputs(inputMap: VectorMultimap[String, AnyRef]): Future[VectorMultimap[String, AnyRef]] =
    Future.sequence(
      proxiedParams
        .filter(inputsToBeReplacedIn(inputMap))
        .map(_.supplyParamValue(originalModelMetadata))
    ).map(addExtraInputsTo(inputMap))

  private def supplyNonProvidedComposedInputs(inputMap: VectorMultimap[String, AnyRef]): Future[VectorMultimap[String, AnyRef]] =
    Future.sequence(compositeProxiedParams
      .map(_.supplyCompositeParamValues(originalModelMetadata))
    )
      .map(_.foldLeft(inputMap) { (acc, composedValues) =>
        addExtraInputsTo(acc)(composedValues)
      })

  private def inputsToBeReplacedIn(inputMap: VectorMultimap[String, AnyRef]): ProxiedModelInputParam => Boolean =
    param => !inputMap.containsKey(param.paramName.name)

  private def addExtraInputsTo(inputMap: VectorMultimap[String, AnyRef])(extraInputs: Iterable[(String, AnyRef)]): VectorMultimap[String, AnyRef] =
    extraInputs.foldLeft(inputMap) { (acc, value) => acc.add(value._1, value._2) }
}
