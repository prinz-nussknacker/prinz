package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelMetadata, ModelName, ModelVersion}
import pl.touk.nussknacker.engine.util.SynchronousExecutionContext.ctx
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.Future

class ProxiedInputModel(model: Model,
                        params: Iterable[ProxiedModelInputParam],
                        composedParams: Iterable[ProxiedModelComposedInputParam[AnyRef]]) extends Model {

  private val originalModelInstance = model.toModelInstance

  private val proxiedParams = params

  private val composedProxiedParams = composedParams

  private val proxiedName = new ModelName(s"Proxied-${model.getName.internal}")

  private val modelMetadata = ModelMetadata(model.getName, model.getVersion, originalModelInstance.getSignature)

  private val proxiedModelInstance = new ModelInstance(
    originalModelInstance.model,
    originalModelInstance.signatureProvider
  ) {
    override def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult = {
      val addInputParams = supplyNonProvidedInputs(inputMap)
      val addComposedParams = addInputParams.flatMap(supplyNonProvidedComposedInputs)
      addComposedParams.flatMap(originalModelInstance.run)
    }
  }

  override def getName: ModelName = proxiedName

  override def getVersion: ModelVersion = model.getVersion

  override def toModelInstance: ModelInstance = proxiedModelInstance

  private def supplyNonProvidedInputs(inputMap: VectorMultimap[String, AnyRef]): Future[VectorMultimap[String, AnyRef]] =
    Future.sequence(
      proxiedParams
        .filter(inputsToBeReplacedIn(inputMap))
        .map(supplyParamValue)
    ).map(addExtraInputsTo(inputMap))

  private def supplyNonProvidedComposedInputs(inputMap: VectorMultimap[String, AnyRef]): Future[VectorMultimap[String, AnyRef]] =
    Future.sequence(composedProxiedParams.map(supplyComposedParamValues))
      .map(_.foldLeft(inputMap) { (acc, composedValues) =>
        addExtraInputsTo(acc)(composedValues)
      })

  private def supplyParamValue(param: ProxiedModelInputParam): Future[(String, AnyRef)] = {
    val paramName = param.paramName
    val metadata = ModelInputParamMetadata(paramName, modelMetadata)
    val resultFuture = param.paramSupplier(metadata)
    resultFuture.map { result =>
      (paramName.name, result)
    }
  }

  private def inputsToBeReplacedIn(inputMap: VectorMultimap[String, AnyRef]): ProxiedModelInputParam => Boolean =
    param => !inputMap.containsKey(param.paramName.name)

  private def supplyComposedParamValues(param: ProxiedModelComposedInputParam[AnyRef]): Future[Iterable[(String, AnyRef)]] = {
    param.paramsSupplier(modelMetadata)
      .flatMap(param.paramsExtractor(_))
      .map { iter => iter.map { case (name, value) => (name.name, value) } }
  }

  private def addExtraInputsTo(inputMap: VectorMultimap[String, AnyRef])(extraInputs: Iterable[(String, AnyRef)]): VectorMultimap[String, AnyRef] =
    extraInputs.foldLeft(inputMap) { (acc, value) => acc.add(value._1, value._2) }
}
