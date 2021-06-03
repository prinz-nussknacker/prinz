package pl.touk.nussknacker.prinz.engine

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.engine.api.definition.{Parameter, ServiceWithExplicitMethod}
import pl.touk.nussknacker.engine.api.test.InvocationCollectors
import pl.touk.nussknacker.engine.api.typed.typing
import pl.touk.nussknacker.engine.api.{ContextId, MetaData}
import pl.touk.nussknacker.prinz.model.Model
import pl.touk.nussknacker.prinz.model.ModelInstance.ModelInputData
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

final case class PrinzEnricher(private val model: Model)
  extends ServiceWithExplicitMethod
    with LazyLogging {

  private lazy val modelInstance = {
    model.toModelInstance
  }

  override def invokeService(params: List[AnyRef])
                            (implicit ec: ExecutionContext, collector: InvocationCollectors.ServiceInvocationCollector,
                             metaData: MetaData, contextId: ContextId): Future[AnyRef] = {
    val inputMap = createInputMap(params)
    modelInstance.run(inputMap).map {
      case Right(runResult) => runResult
      case Left(exc) => throw exc
    }
  }

  override def parameterDefinition: List[Parameter] =
    model
      .getMetadata
      .signature
      .toInputParameterDefinition

  override def returnType: typing.TypingResult =
    model
      .getMetadata
      .signature
      .toOutputTypedObjectTypingResult

  def createInputMap(inputs: List[AnyRef]): ModelInputData =
    VectorMultimap(parameterDefinition.map(_.name) zip inputs)
}
