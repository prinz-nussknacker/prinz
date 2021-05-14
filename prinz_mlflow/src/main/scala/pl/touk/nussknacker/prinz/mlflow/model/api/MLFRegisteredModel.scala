package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.prinz.mlflow.model.api.MLFRegisteredModel.createMLFModelSignatureLocationMetadata
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository
import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelMetadata, ModelName, ModelSignature, ModelSignatureLocationMetadata, ModelVersion, SignatureProvider}

import java.time.Instant

final case class MLFRegisteredModel(name: MLFRegisteredModelName,
                                    creationTimestamp: Instant,
                                    lastUpdatedTimestamp: Instant,
                                    latestVersions: List[MLFRegisteredModelVersion],
                                    private val repository: MLFModelRepository) extends Model {

  override val signatureOption: ProvideSignatureResult = MLFSignatureProvider(repository.config)
    .provideSignature(createMLFModelSignatureLocationMetadata(name, latestVersions))

  override def getMetadata: ModelMetadata = MLFModelSignatureLocationMetadata(name, latestVersions.maxBy(_.lastUpdatedTimestamp))

  override def getName: MLFRegisteredModelName = name

  override def getVersion: MLFRegisteredModelVersion = latestVersions.maxBy(_.lastUpdatedTimestamp)

  def getVersionName: MLFRegisteredModelVersionName = MLFRegisteredModelVersionName(latestVersions.maxBy(_.lastUpdatedTimestamp).name)

  override def toModelInstance: MLFModelInstance = MLFModelInstance(repository.config, this)
}

object MLFRegisteredModel {

  private def createMLFModelSignatureLocationMetadata(name: MLFRegisteredModelName,
                                                      latestVersions: List[MLFRegisteredModelVersion]):
  MLFModelSignatureLocationMetadata = MLFModelSignatureLocationMetadata(name, )
}

final case class MLFRegisteredModelName(name: String) extends ModelName(name)

final case class MLFRegisteredModelVersionName(name: String)

final case class MLFRegisteredModelVersion(name: String,
                                           version: String,
                                           creationTimestamp: Instant,
                                           lastUpdatedTimestamp: Instant,
                                           currentStage: String,
                                           source: String,
                                           runId: String,
                                           status: String) extends ModelVersion

final case class MLFModelSignatureLocationMetadata(name: MLFRegisteredModelName,
                                                   version: MLFRegisteredModelVersion)
  extends ModelSignatureLocationMetadata with ModelMetadata
