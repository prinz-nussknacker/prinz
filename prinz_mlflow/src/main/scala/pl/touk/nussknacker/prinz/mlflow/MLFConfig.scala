package pl.touk.nussknacker.prinz.mlflow

import java.net.URL
import com.typesafe.config.Config
import pl.touk.nussknacker.prinz.mlflow.model.api.{LocalMLFModelLocationStrategy, MLFModelLocationStrategy}
import pl.touk.nussknacker.prinz.util.config.ConfigReader.{getConfigValue, getString, getUrl, url}

case class MLFConfig(private implicit val config: Config) {

  private implicit val BASE_CONFIG_PATH: String = "mlflow."

  val modelLocationStrategy: MLFModelLocationStrategy = LocalMLFModelLocationStrategy

  val baseApiPath: String = s"/api/2.0/mlflow"

  val basePreviewApiPath: String = s"/api/2.0/preview/mlflow"

  val serverUrl: URL = getConfigValue("serverUrl", url("http://localhost:5000"), getUrl)

  val servedModelsUrl: URL = getConfigValue("servedModelsUrl", url("http://localhost:5000"), getUrl)

  val s3AccessKey: String = getConfigValue("s3AccessKey", "mlflow-key", getString)

  val s3SecretKey: String = getConfigValue("s3SecretKey", "mlflow-secret", getString)

  val s3Url: URL = getConfigValue("s3Url", url("http://localhost:9000"), getUrl)

  val s3ModelRelativePath: String = getConfigValue("s3ModelRelativePath", "/model/MLmodel", getString)

  val s3BucketName: String = getConfigValue("s3BucketName", "mlflow", getString)
}
