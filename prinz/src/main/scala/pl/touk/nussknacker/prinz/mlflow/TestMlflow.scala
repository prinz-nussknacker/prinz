package pl.touk.nussknacker.prinz.mlflow

import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.model.RegisteredModel
import pl.touk.nussknacker.prinz.mlflow.repository.MLFRepositoryRestClient
import MlflowConstants.DEFAULT_PORT
import pl.touk.nussknacker.prinz.model.repository.ModelRepository

object TestMlflow {

  def main(args: Array[String]): Unit = {
    val restClient: MLFRepositoryRestClient = MLFRepositoryRestClient(new URL(s"http://localhost:$DEFAULT_PORT"))
    val models2 = restClient.listModels(10, 1)
    println(models2)

    val repositoryClient: ModelRepository = MLFRepositoryRestClient(new URL(s"http://localhost:$DEFAULT_PORT"))
    val models = repositoryClient.listModels()
    println(models)

    val testModel = models.right.get.head
    val name = testModel.getName
    println(s"Head model's name: $name")

    val model = repositoryClient.getModel(name)
    println(model)
  }
}
