package pl.touk.nussknacker.prinz.enrichers

import pl.touk.nussknacker.engine.api.definition.{Parameter, ServiceWithExplicitMethod}
import pl.touk.nussknacker.engine.api.test.InvocationCollectors
import pl.touk.nussknacker.engine.api.typed.typing
import pl.touk.nussknacker.engine.api.{ContextId, MetaData}
import pl.touk.nussknacker.prinz.model.Model

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

case class PrinzEnricher(private val model: Model) extends ServiceWithExplicitMethod {

  private val modelInstance = model.toModelInstance

  private val inputNames = modelInstance
    .getSignature
    .getInputNames
    .map(_.name)

  override def invokeService(params: List[AnyRef])
                            (implicit ec: ExecutionContext, collector: InvocationCollectors.ServiceInvocationCollector,
                             metaData: MetaData, contextId: ContextId): Future[AnyRef] = {
    // TODO map params list and collect them according to columns order
    modelInstance.run(inputNames, List())
  }

  override def parameterDefinition: List[Parameter] = modelInstance.getSignature.toInputParameterDefinition

  override def returnType: typing.TypingResult = modelInstance.getSignature.getOutputDefinition
}
