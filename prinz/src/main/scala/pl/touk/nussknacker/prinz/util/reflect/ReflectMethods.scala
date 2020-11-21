package pl.touk.nussknacker.prinz.util.reflect

import scala.reflect.runtime.universe.{MethodSymbol, runtimeMirror}

object ReflectMethods {

  def invoke(m: MethodSymbol)(o: Any)(args: Any*): Any = {
    val mirror = runtimeMirror(o.getClass.getClassLoader).reflect(o)
    mirror.reflectMethod(m)(args: _*)
  }
}
