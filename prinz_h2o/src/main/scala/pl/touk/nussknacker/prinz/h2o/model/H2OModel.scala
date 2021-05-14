package pl.touk.nussknacker.prinz.h2o.model

import hex.genmodel.{GenModel, ModelMojoReader, MojoReaderBackend, MojoReaderBackendFactory}
import hex.genmodel.MojoReaderBackendFactory.CachingStrategy
import hex.genmodel.easy.EasyPredictModelWrapper
import pl.touk.nussknacker.prinz.h2o.model.H2OModel.loadGenModel
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelPayload
import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelSignatureLocationMetadata, ModelVersion}

import java.net.URL

class H2OModel(payload: H2OModelPayload, cachingStrategy: CachingStrategy) extends Model {

    private val modelReaderBackend: MojoReaderBackend =
        MojoReaderBackendFactory.createReaderBackend(payload.path, cachingStrategy)

    private val genModel: GenModel = ModelMojoReader.readFrom(modelReaderBackend)

    val modelWrapper: EasyPredictModelWrapper = new EasyPredictModelWrapper(genModel)

    override def toModelInstance: ModelInstance = H2OModelInstance(modelWrapper, this)

    override protected val signatureOption: ProvideSignatureResult = {
        val model = loadGenModel(payload.path, cachingStrategy)
        val metadata = H2OModelSignatureLocationMetadata(model)
        H2OSignatureProvider.provideSignature(metadata)
    }

    override protected def getName: ModelName = H2OModelName(payload.name)

    override protected def getVersion: ModelVersion = H2OModelVersion(payload.version)
}

object H2OModel {

    private def loadGenModel(modelUrl: URL, cachingStrategy: CachingStrategy): GenModel = {
        val readerBackend = MojoReaderBackendFactory.createReaderBackend(modelUrl, cachingStrategy)
        ModelMojoReader.readFrom(readerBackend)
    }
}

final case class H2OModelName(name: String) extends ModelName(name)

final case class H2OModelVersion(version: String) extends ModelVersion

final case class H2OModelSignatureLocationMetadata(genModel: GenModel) extends ModelSignatureLocationMetadata
