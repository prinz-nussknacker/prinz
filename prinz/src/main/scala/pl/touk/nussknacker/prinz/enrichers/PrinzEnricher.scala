package pl.touk.nussknacker.prinz.enrichers

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.engine.api.definition.{Parameter, ServiceWithExplicitMethod}
import pl.touk.nussknacker.engine.api.test.InvocationCollectors
import pl.touk.nussknacker.engine.api.typed.typing
import pl.touk.nussknacker.engine.api.{ContextId, MetaData}
import pl.touk.nussknacker.prinz.model.Model
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

case class PrinzEnricher(private val model: Model) extends ServiceWithExplicitMethod with LazyLogging {

  private val modelInstance = model.toModelInstance

  override def invokeService(params: List[AnyRef])
                            (implicit ec: ExecutionContext, collector: InvocationCollectors.ServiceInvocationCollector,
                             metaData: MetaData, contextId: ContextId): Future[AnyRef] = {
    // TODO map params list and collect them according to columns order
    logger.info("____STARTING_________")
    val map = createInputMap(params)
    logger.info(params.toString)
    logger.info(map.toString)
    modelInstance.run(map).map(response => response.toOption.get.head.asInstanceOf[AnyRef])
  }

  override def parameterDefinition: List[Parameter] = modelInstance.getSignature.toInputParameterDefinition

  override def returnType: typing.TypingResult = modelInstance.getSignature.getOutputDefinition

  def createInputMap(inputs: List[AnyRef]): VectorMultimap[String, AnyRef] =
    VectorMultimap(parameterDefinition.map(_.name) zip inputs)
}
