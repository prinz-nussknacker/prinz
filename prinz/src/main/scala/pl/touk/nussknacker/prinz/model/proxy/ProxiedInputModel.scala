package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelMetadata, ModelName, ModelVersion}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

class ProxiedInputModel(model: Model,
                        params: Iterable[ProxiedModelInputParam]) extends Model {

  private val originalModelInstance = model.toModelInstance

  private val proxiedParams = params

  private val modelMetadata = ModelMetadata(model.getName, model.getVersion)

  private val proxiedModelInstance = new ModelInstance(
    originalModelInstance.model,
    originalModelInstance.signatureProvider
  ) {
    override def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult = {
      val modifiedInputMap = supplyNonProvidedInputs(inputMap)
      originalModelInstance.run(modifiedInputMap)
    }
  }

  override def getName: ModelName = {
    val originalName = model.getName.internal
    new ModelName(s"Proxied-$originalName")
  }

  override def getVersion: ModelVersion = model.getVersion

  override def toModelInstance: ModelInstance = proxiedModelInstance

  private def supplyNonProvidedInputs(inputMap: VectorMultimap[String, AnyRef]): VectorMultimap[String, AnyRef] =
    proxiedParams
      .filter(inputsToBeReplacedIn(inputMap))
      .map(supplyParamValue)
      .foldLeft(inputMap) { (acc, value) => acc.add(value._1, value._2) }

  private def supplyParamValue(param: ProxiedModelInputParam): (String, AnyRef) = {
    val paramName = param.paramName
    val metadata = ModelInputParamMetadata(paramName, modelMetadata)
    val result = param.paramSupplier(metadata)
    (paramName, result)
  }

  private def inputsToBeReplacedIn(inputMap: VectorMultimap[String, AnyRef]): ProxiedModelInputParam => Boolean =
    param => !inputMap.containsKey(param.paramName) || param.overwriteProvided
}
