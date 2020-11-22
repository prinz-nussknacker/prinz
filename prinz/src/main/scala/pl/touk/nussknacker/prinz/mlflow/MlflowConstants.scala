package pl.touk.nussknacker.prinz.mlflow

import java.net.URL

object MlflowConstants {
  val DEFAULT_USER: String = "mlflow.user"
  val DEFAULT_PORT: Int = 5000
  val BASE_API_PATH: String = s"/api/2.0/mlflow"
  val BASE_PREVIEW_API_PATH: String = s"/api/2.0/preview/mlflow"
  val S3_ACCESS_KEY: String = "mlflow-key"
  val S3_SECRET_KEY: String = "mlflow-secret"
  val S3_URL: URL = new URL("http://localhost:9000")
  val BUCKET_NAME: String = "mlflow"
  val EXPERIMENT_ID: Int = 0
}
