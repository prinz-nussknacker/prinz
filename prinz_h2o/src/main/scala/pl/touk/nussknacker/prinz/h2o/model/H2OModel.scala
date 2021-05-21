package pl.touk.nussknacker.prinz.h2o.model

import hex.genmodel.{GenModel, ModelMojoReader, MojoReaderBackendFactory}
import hex.genmodel.MojoReaderBackendFactory.CachingStrategy
import pl.touk.nussknacker.prinz.h2o.model.H2OModel.loadGenModel
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelPayload
import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelSignatureLocationMetadata, ModelVersion}

import java.net.URL

final class H2OModel(payload: H2OModelPayload, cachingStrategy: CachingStrategy) extends Model {

    override def toModelInstance: ModelInstance = {
        val modelWrapper = H2OModelWrapperExtractor.extractModelWrapper(payload, cachingStrategy)
        H2OModelInstance(modelWrapper, this)
    }

    override protected val signatureOption: ProvideSignatureResult = {
        val model = loadGenModel(payload.path, cachingStrategy)
        val metadata = H2OModelSignatureLocationMetadata(model)
        H2OSignatureProvider.provideSignature(metadata)
    }

    override protected val name: ModelName = H2OModelName(payload.name)

    override protected val version: ModelVersion = H2OModelVersion(payload.version)
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
