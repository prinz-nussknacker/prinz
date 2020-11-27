package pl.touk.nussknacker.prinz.mlflow.model

import java.net.URL

import pl.touk.nussknacker.prinz.model.ModelInstance
import pl.touk.nussknacker.prinz.util.http.RestJsonClient

class MLFModelInstance(runUrl: URL) extends ModelInstance {
  private val restClient = new RestJsonClient(s"$runUrl")

  override def run[DATA_TYPE](columns: List[String], data: List[DATA_TYPE]): Either[RestModelRunException, Float] =
    restClient.postJsonBody[RunBody[DATA_TYPE], List[Float]]("/invocations", RunBody(columns, List(data)))
      .right.map(_.head)
      .left.map(new RestModelRunException(_))

  override def getSignature(): List[(String, String)] = ("Not", "implemented")::("yet", ".")::Nil
}

case class RunBody[D](columns: List[String], data: List[List[D]])
