package pl.touk.nussknacker.prinz.h2o.model

import hex.genmodel.{GenModel, ModelMojoReader, MojoReaderBackendFactory}
import hex.genmodel.MojoReaderBackendFactory.CachingStrategy
import hex.genmodel.easy.EasyPredictModelWrapper
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelPayload

object H2OModelWrapperExtractor {

  def extractModelWrapper(payload: H2OModelPayload, cachingStrategy: CachingStrategy): EasyPredictModelWrapper = {
    val genModel = extractGenModel(payload, cachingStrategy)
    new EasyPredictModelWrapper(genModel)
  }

  def extractGenModel(payload: H2OModelPayload, cachingStrategy: CachingStrategy): GenModel = {
    val modelReaderBackend = MojoReaderBackendFactory.createReaderBackend(payload.path, cachingStrategy)
    ModelMojoReader.readFrom(modelReaderBackend)
  }
}
