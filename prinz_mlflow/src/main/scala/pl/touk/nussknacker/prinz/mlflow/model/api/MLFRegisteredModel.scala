package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.prinz.mlflow.model.api.MLFRegisteredModel.extractLatestVersion
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository
import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelName, ModelSignatureLocationMetadata, ModelVersion}

import java.time.Instant

final case class MLFRegisteredModel(name: MLFRegisteredModelName,
                                    creationTimestamp: Instant,
                                    lastUpdatedTimestamp: Instant,
                                    latestVersions: List[MLFRegisteredModelVersion],
                                    private val repository: MLFModelRepository) extends Model {

  def getVersionName: MLFRegisteredModelVersionName = MLFRegisteredModelVersionName(latestVersions.maxBy(_.lastUpdatedTimestamp).name)

  override def toModelInstance: MLFModelInstance = MLFModelInstance(repository.config, this)

  override protected val signatureOption: ProvideSignatureResult = MLFSignatureProvider(repository.config)
    .provideSignature(MLFModelSignatureLocationMetadata(name, extractLatestVersion(latestVersions)))

  override protected def getName: MLFRegisteredModelName = name

  override protected def getVersion: MLFRegisteredModelVersion = extractLatestVersion(latestVersions)
}

object MLFRegisteredModel {

  private def extractLatestVersion(latestVersions: List[MLFRegisteredModelVersion]): MLFRegisteredModelVersion =
    latestVersions.maxBy(_.lastUpdatedTimestamp)
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
  extends ModelSignatureLocationMetadata
