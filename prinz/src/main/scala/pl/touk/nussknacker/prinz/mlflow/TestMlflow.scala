package pl.touk.nussknacker.prinz.mlflow

import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.model.RegisteredModel
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepositoryRestClient
import MlflowConstants.DEFAULT_PORT
import pl.touk.nussknacker.prinz.model.repository.ModelRepository

object TestMlflow {

  def main(args: Array[String]): Unit = {
    val url = new URL(s"http://localhost:$DEFAULT_PORT")
    val max = 10
    val page = 1

    val restClient: MLFRepositoryRestClient = MLFRepositoryRestClient(url)
    val models2 = restClient.listModels(max, page)
    println(models2)

    val repositoryClient: ModelRepository = MLFRepositoryRestClient(url)
    val models = repositoryClient.listModels()
    println(models)

    val testModel = models.right.get.head
    val name = testModel.getName
    println(s"Head model's name: $name")

    val model = repositoryClient.getModel(name)
    println(model)
  }
}
