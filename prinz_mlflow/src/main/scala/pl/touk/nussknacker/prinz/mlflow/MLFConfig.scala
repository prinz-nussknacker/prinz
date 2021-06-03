package pl.touk.nussknacker.prinz.mlflow

import com.typesafe.config.Config
import pl.touk.nussknacker.prinz.mlflow.model.api.{LocalMLFModelLocationStrategy, MLFModelLocationStrategy}
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getString, getUrl}

import java.net.URL

final case class MLFConfig(modelLocationStrategy: MLFModelLocationStrategy = LocalMLFModelLocationStrategy)
                          (private implicit val config: Config) {

  private implicit val BASE_CONFIG_PATH: String = "mlfConfig"

  val baseApiPath: String = "/api/2.0/mlflow"

  val basePreviewApiPath: String = "/api/2.0/preview/mlflow"

  val serverUrl: URL = getConfigValue("serverUrl", getUrl)

  val servedModelsUrl: URL = getConfigValue("servedModelsUrl", getUrl)

  val s3AccessKey: String = getConfigValue("s3AccessKey", getString)

  val s3SecretKey: String = getConfigValue("s3SecretKey", getString)

  val s3Url: URL = getConfigValue("s3Url", getUrl)

  val s3ModelRelativePath: String = getConfigValue("s3ModelRelativePath", getString)

  val s3BucketName: String = getConfigValue("s3BucketName", getString)
}
