package pl.touk.nussknacker.prinz.h2o.model

import hex.genmodel.GenModel
import hex.genmodel.MojoReaderBackendFactory.CachingStrategy
import pl.touk.nussknacker.prinz.h2o.repository.H2OModelPayload
import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelSignatureLocationMetadata, ModelVersion}

final class H2OModel(payload: H2OModelPayload, cachingStrategy: CachingStrategy) extends Model {

    override def toModelInstance: ModelInstance = {
        val modelWrapper = H2OModelWrapperExtractor.extractModelWrapper(payload, cachingStrategy)
        H2OModelInstance(modelWrapper, this)
    }

    override protected val signatureOption: ProvideSignatureResult = {
        val genModel = H2OModelWrapperExtractor.extractGenModel(payload, cachingStrategy)
        val metadata = H2OModelSignatureLocationMetadata(genModel)
        H2OSignatureProvider.provideSignature(metadata)
    }

    override protected val name: ModelName = H2OModelName(payload.name)

    override protected val version: ModelVersion = H2OModelVersion(payload.version)
}

final case class H2OModelName(name: String) extends ModelName(name)

final case class H2OModelVersion(version: String) extends ModelVersion {

    override def toString: String = version
}

final case class H2OModelSignatureLocationMetadata(genModel: GenModel) extends ModelSignatureLocationMetadata
