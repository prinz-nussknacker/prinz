package pl.touk.nussknacker.prinz.mlflow.model.api

import pl.touk.nussknacker.prinz.mlflow.model.api.MLFRegisteredModel.extractLatestVersion
import pl.touk.nussknacker.prinz.mlflow.repository.MLFModelRepository
import pl.touk.nussknacker.prinz.model.SignatureProvider.ProvideSignatureResult
import pl.touk.nussknacker.prinz.model.{Model, ModelName, ModelSignatureLocationMetadata, ModelVersion}

import java.time.Instant

final class MLFRegisteredModel(val registeredModelName: MLFRegisteredModelName,
                               val creationTimestamp: Instant,
                               val lastUpdatedTimestamp: Instant,
                               val latestVersions: List[MLFRegisteredModelVersion],
                               private val repository: MLFModelRepository) extends Model {

  override def toModelInstance: MLFModelInstance = MLFModelInstance(repository.config, this)

  override protected val signatureOption: ProvideSignatureResult = MLFSignatureProvider(repository.config)
    .provideSignature(MLFModelSignatureLocationMetadata(registeredModelName, extractLatestVersion(latestVersions)))

  override protected val version: ModelVersion = extractLatestVersion(latestVersions)

  val versionName: MLFRegisteredModelVersionName = MLFRegisteredModelVersionName(latestVersions.maxBy(_.lastUpdatedTimestamp).name)

  override protected val name: MLFRegisteredModelName = registeredModelName
}

object MLFRegisteredModel {

  def apply(name: MLFRegisteredModelName,
            creationTimestamp: Instant,
            lastUpdatedTimestamp: Instant,
            latestVersions: List[MLFRegisteredModelVersion],
            repository: MLFModelRepository): MLFRegisteredModel =
    new MLFRegisteredModel(name, creationTimestamp, lastUpdatedTimestamp, latestVersions, repository)

  private def extractLatestVersion(latestVersions: List[MLFRegisteredModelVersion]): MLFRegisteredModelVersion =
    latestVersions.maxBy(_.lastUpdatedTimestamp)
}

final class MLFRegisteredModelName(name: String) extends ModelName(name)

final case class MLFRegisteredModelVersionName(internal: String)

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
