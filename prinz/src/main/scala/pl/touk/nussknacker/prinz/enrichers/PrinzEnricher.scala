package pl.touk.nussknacker.prinz.enrichers

import pl.touk.nussknacker.engine.api.definition.{Parameter, ServiceWithExplicitMethod}
import pl.touk.nussknacker.engine.api.test.InvocationCollectors
import pl.touk.nussknacker.engine.api.typed.typing
import pl.touk.nussknacker.engine.api.typed.typing.Typed
import pl.touk.nussknacker.engine.api.{ContextId, MetaData}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class PrinzEnricher extends ServiceWithExplicitMethod {

  override def invokeService(params: List[AnyRef])
                            (implicit ec: ExecutionContext, collector: InvocationCollectors.ServiceInvocationCollector,
                             metaData: MetaData, contextId: ContextId): Future[AnyRef] = {
    val res = ().asInstanceOf[AnyRef]
    Future.successful(res)
  }

  override def parameterDefinition: List[Parameter] =
    List(
      Parameter[Float]("inputA"),
      Parameter[Float]("inputB")
    )

  override def returnType: typing.TypingResult = Typed[Unit]
}