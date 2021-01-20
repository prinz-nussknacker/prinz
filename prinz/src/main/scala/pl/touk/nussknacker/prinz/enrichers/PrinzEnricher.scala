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
    val inputMap = createInputMap(params)
    logger.info("input params in enricher: " + params)
    logger.info("input map for model run: " + inputMap)
    modelInstance.run(inputMap).map(response => response.toOption.get)
  }

  override def parameterDefinition: List[Parameter] = modelInstance.getSignature.toInputParameterDefinition

  override def returnType: typing.TypingResult = modelInstance.getSignature.getOutputDefinition

  def createInputMap(inputs: List[AnyRef]): VectorMultimap[String, AnyRef] =
    VectorMultimap(parameterDefinition.map(_.name) zip inputs)
}
