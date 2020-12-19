package pl.touk.nussknacker.prinz.mlflow

import java.net.URL

import com.typesafe.config.{Config, ConfigFactory}
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getInt, getString, getUrl, url}

object MLFConfig {

  private implicit val CONFIG: Config = ConfigFactory.load()

  private implicit val BASE_CONFIG_PATH: String = "mlflow."

  val baseApiPath: String = s"/api/2.0/mlflow"

  val basePreviewApiPath: String = s"/api/2.0/preview/mlflow"

  val serverUrl: URL = getConfigValue("serverUrl", url("http://localhost:5000"), getUrl)

  val servedModelsUrl: URL = getConfigValue("servedModelsUrl", url("http://localhost:5000"), getUrl)

  val s3AccessKey: String = getConfigValue("s3AccessKey", "mlflow-key", getString)

  val s3SecretKey: String = getConfigValue("s3SecretKey", "mlflow-secret", getString)

  val s3Url: URL = getConfigValue("s3Url", url("http://localhost:9000"), getUrl)

  val s3ModelRelativePath: String = getConfigValue("s3ModelRelativePath", "/model/MLmodel", getString)

  val s3BucketName: String = getConfigValue("s3BucketName", "mlflow", getString)

  val experimentId: Int = getConfigValue("experimentId", 0, getInt)

  val mlflowProxyUrl: String = getConfigValue("mlflowProxyUrl", "http://proxy:5000", getString)
}
