package pl.touk.nussknacker.prinz.model

final case class ModelMetadata(modelName: ModelName,
                               modelVersion: ModelVersion,
                               signature: ModelSignature)

trait ModelSignatureLocationMetadata