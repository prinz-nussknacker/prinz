package pl.touk.nussknacker.prinz.util.http

import pl.touk.nussknacker.prinz.util.reflect.ReflectFields.getAccessorsWithValues
import scala.reflect.runtime.universe.TypeTag

abstract class RequestParams[T: TypeTag] {

  def getParamsMap: Map[String, String] = getAccessorsWithValues[T](this)
    .map { case (field, value) => (field, value.toString) }.toMap
}

object EmptyRequestParams extends RequestParams[Any] {

  override def getParamsMap: Map[String, String] = Map.empty
}
