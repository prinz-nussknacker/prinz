package pl.touk.nussknacker.prinz.mlflow.model

import java.net.URL

import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelSignature}
import pl.touk.nussknacker.prinz.util.http.RestJsonClient

class MLFModelInstance(runUrl: URL, signature: ModelSignature) extends ModelInstance {

  private val mlflowClient = RestJsonClient(s"$runUrl")

  override def run[DATA_TYPE](columns: List[String], data: List[DATA_TYPE]): Either[RestModelRunException, Float] =
    mlflowClient.postJsonBody[RunBody[DATA_TYPE], List[Float]]("/invocations", RunBody(columns, List(data)))
      .right.map(_.head)
      .left.map(new RestModelRunException(_))

  override def getSignature: ModelSignature = signature
}

case class RunBody[DATA_TYPE](columns: List[String], data: List[List[DATA_TYPE]])
