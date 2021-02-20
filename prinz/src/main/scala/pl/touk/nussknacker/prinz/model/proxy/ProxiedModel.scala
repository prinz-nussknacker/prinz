package pl.touk.nussknacker.prinz.model.proxy

import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

class ProxiedModel(model: Model, params: ProxiedModelInputParam*) extends Model {

  private val originalModelInstance = model.toModelInstance

  private val proxiedParams = params.toList

  private val proxiedModelInstance = new ModelInstance(
    originalModelInstance.model,
    originalModelInstance.signatureProvider
  ) {

    override def run(inputMap: VectorMultimap[String, AnyRef]): ModelRunResult = {
      val modifiedInputMap = addNonProvidedInputs(inputMap)
      originalModelInstance.run(modifiedInputMap)
    }
  }

  override def getName: ModelName = {
    val originalName = model.getName.internal
    new ModelName(s"Proxied-$originalName")
  }

  override def getVersion: ModelVersion = model.getVersion

  override def toModelInstance: ModelInstance = proxiedModelInstance

  private def addNonProvidedInputs(inputMap: VectorMultimap[String, AnyRef]): VectorMultimap[String, AnyRef] = {
    proxiedParams
      .filter(param => inputMap.containsKey(param.paramName))
      .foreach(param => inputMap.add(param.paramName, param.paramSupplier()))
    inputMap
  }
}
