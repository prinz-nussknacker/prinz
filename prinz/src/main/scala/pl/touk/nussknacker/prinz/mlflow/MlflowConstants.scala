package pl.touk.nussknacker.prinz.mlflow

//TODO: Read from config file
object MlflowConstants {
  val DEFAULT_USER: String = "mlflow.user"
  val DEFAULT_PORT: Int = 5000
  val BASE_API_PATH: String = s"/api/2.0/mlflow"
  val BASE_PREVIEW_API_PATH: String = s"/api/2.0/preview/mlflow"
}
