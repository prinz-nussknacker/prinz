package pl.touk.nussknacker.prinz.h2o.model

import hex.genmodel.{GenModel, ModelMojoReader, MojoReaderBackend, MojoReaderBackendFactory}
import hex.genmodel.MojoReaderBackendFactory.CachingStrategy
import hex.genmodel.easy.EasyPredictModelWrapper
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelPayload
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}

class H2OModel(payload: H2OModelPayload, cachingStrategy: CachingStrategy) extends Model {

    private val modelReaderBackend: MojoReaderBackend =
        MojoReaderBackendFactory.createReaderBackend(payload.path, cachingStrategy)

    private val genModel: GenModel = ModelMojoReader.readFrom(modelReaderBackend)

    val modelWrapper: EasyPredictModelWrapper = new EasyPredictModelWrapper(genModel)

    override def getName: ModelName = H2OModelName(payload.name)

    override def getVersion: ModelVersion = H2OModelVersion(payload.version)

    override def toModelInstance: ModelInstance = H2OModelInstance(modelWrapper, this)
}

case class H2OModelName(name: String) extends ModelName(name)

case class H2OModelVersion(version: String) extends ModelVersion
