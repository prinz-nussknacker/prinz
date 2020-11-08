package pl.touk.nussknacker.prinz.mlflow

import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.rest.MlflowConstants.DEFAULT_PORT
import pl.touk.nussknacker.prinz.mlflow.rest.client.MlflowClient
import pl.touk.nussknacker.prinz.util.http.RestClientException

object TestMlflow {

  def main(args: Array[String]): Unit = {
    val client = MlflowClient(new URL(s"http://localhost:$DEFAULT_PORT"))
    val models = client.listRegisteredModels
    println(models)
  }
}
