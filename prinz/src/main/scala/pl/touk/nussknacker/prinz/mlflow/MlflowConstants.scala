package pl.touk.nussknacker.prinz.mlflow

import java.net.URL

object MlflowConstants {
  val DEFAULT_HOST: String = "http://localhost"
  val DEFAULT_PORT: Int = 5000
  val DEFAULT_SERVE_PORT: Int = 1234
  val DEFAULT_URL: URL = new URL(s"$DEFAULT_HOST:$DEFAULT_PORT")
  val DEFAULT_USER: String = "mlflow.user"
  val BASE_API_PATH: String = s"/api/2.0/mlflow"
  val BASE_PREVIEW_API_PATH: String = s"/api/2.0/preview/mlflow"
  val S3_ACCESS_KEY: String = "mlflow-key"
  val S3_SECRET_KEY: String = "mlflow-secret"
  val S3_PORT: Int = 9000
  val S3_URL: URL = new URL(s"$DEFAULT_HOST:$S3_PORT")
  val S3_MODEL_REL_PATH: String = "/artifacts/model/MLmodel"
  val BUCKET_NAME: String = "mlflow"
  val EXPERIMENT_ID: Int = 0
}
