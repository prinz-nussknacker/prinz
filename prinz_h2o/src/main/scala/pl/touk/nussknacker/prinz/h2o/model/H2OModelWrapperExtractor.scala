package pl.touk.nussknacker.prinz.h2o.model

import hex.genmodel.{ModelMojoReader, MojoReaderBackendFactory}
import hex.genmodel.MojoReaderBackendFactory.CachingStrategy
import hex.genmodel.easy.EasyPredictModelWrapper
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelPayload

object H2OModelWrapperExtractor {

  def extractModelWrapper(payload: H2OModelPayload, cachingStrategy: CachingStrategy): EasyPredictModelWrapper = {
    val modelReaderBackend = MojoReaderBackendFactory.createReaderBackend(payload.path, cachingStrategy)
    val genModel = ModelMojoReader.readFrom(modelReaderBackend)
    new EasyPredictModelWrapper(genModel)
  }
}
