package pl.touk.nussknacker.prinz.util.reflect

import pl.touk.nussknacker.prinz.util.reflect.ReflectMethods.invoke

import scala.reflect.runtime.universe.{MethodSymbol, TypeTag, typeOf}

object ReflectFields {

  def getAccessorsWithValues[T: TypeTag](o: Any): List[(String, Any)] =
    typeOf[T].members.collect {
      case m: MethodSymbol if m.isCaseAccessor =>
        (m.name.toString, invoke(m)(o)())
    }.toList
}
