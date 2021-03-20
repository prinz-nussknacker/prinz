package pl.touk.nussknacker.prinz.model.proxy.api

import pl.touk.nussknacker.prinz.model.{ModelMetadata, SignatureName}

final case class ModelInputParamMetadata(paramName: SignatureName, modelMetadata: ModelMetadata)
