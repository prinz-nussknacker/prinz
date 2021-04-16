package pl.touk.nussknacker.prinz.h2o.model

import hex.genmodel.{GenModel, ModelMojoReader, MojoReaderBackendFactory}
import hex.genmodel.MojoReaderBackendFactory.CachingStrategy
import hex.genmodel.easy.EasyPredictModelWrapper
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelPayload
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}

class H2OModel(payload: H2OModelPayload) extends Model {
    // TODO: Should caching strategy be configurable?
    private val modelReaderBackend = MojoReaderBackendFactory.createReaderBackend(payload.inputStream, CachingStrategy.MEMORY)
    private val genModel: GenModel = ModelMojoReader.readFrom(modelReaderBackend)
    val modelWrapper: EasyPredictModelWrapper = new EasyPredictModelWrapper(genModel)

    override def getName: ModelName = ???

    override def getVersion: ModelVersion = ???

    override def toModelInstance: ModelInstance = ???
}
