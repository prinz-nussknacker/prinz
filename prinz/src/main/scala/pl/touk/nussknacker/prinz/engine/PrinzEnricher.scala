package pl.touk.nussknacker.prinz.engine

import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.engine.api.definition.Parameter
import pl.touk.nussknacker.engine.api.test.InvocationCollectors
import pl.touk.nussknacker.engine.api.typed.typing
import pl.touk.nussknacker.engine.api.{ContextId, MetaData}
import pl.touk.nussknacker.engine.util.service.ServiceWithStaticParametersAndReturnType
import pl.touk.nussknacker.prinz.model.Model
import pl.touk.nussknacker.prinz.model.ModelInstance.ModelInputData
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

import scala.concurrent.{ExecutionContext, Future}

final case class PrinzEnricher(private val model: Model)
  extends ServiceWithStaticParametersAndReturnType
    with LazyLogging {

  private lazy val modelInstance = {
    model.toModelInstance
  }

  def invoke(params: Map[String, Any])(implicit ec: ExecutionContext,
                                       collector: InvocationCollectors.ServiceInvocationCollector,
                                       contextId: ContextId,
                                       metaData: MetaData): Future[Any] = {
    val inputMap = createInputMap(params)
    modelInstance.run(inputMap).map {
      case Right(runResult) => runResult
      case Left(exc) => throw exc
    }(ec)
  }

  override def parameters: List[Parameter] =
    model
      .getMetadata
      .signature
      .toInputLazyParameterDefinition

  override def returnType: typing.TypingResult =
    model
      .getMetadata
      .signature
      .toOutputTypedObjectTypingResult

  def createInputMap(inputs: Map[String, Any]): ModelInputData =
    VectorMultimap(parameters.map{p => (p.name, inputs.getOrElse(p.name, throw new IllegalArgumentException(s"Missing param: ${p.name}.")))})

}
