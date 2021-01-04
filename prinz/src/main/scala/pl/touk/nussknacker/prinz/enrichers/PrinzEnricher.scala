package pl.touk.nussknacker.prinz.enrichers

import pl.touk.nussknacker.engine.api.definition.{Parameter, ServiceWithExplicitMethod}
import pl.touk.nussknacker.engine.api.test.InvocationCollectors
import pl.touk.nussknacker.engine.api.typed.typing
import pl.touk.nussknacker.engine.api.typed.typing.Typed
import pl.touk.nussknacker.engine.api.{ContextId, MetaData}
import pl.touk.nussknacker.prinz.enrichers.PrinzEnricher.extractNussknackerSignature
import pl.touk.nussknacker.prinz.model.{Model, SignatureName, SignatureType}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

case class PrinzEnricher(private val model: Model) extends ServiceWithExplicitMethod {

  private val modelInstance = model.toModelInstance

  override def invokeService(params: List[AnyRef])
                            (implicit ec: ExecutionContext, collector: InvocationCollectors.ServiceInvocationCollector,
                             metaData: MetaData, contextId: ContextId): Future[AnyRef] = {
    val res = params.head.asInstanceOf[Double].toString
    Future.successful(res)
  }

  override def parameterDefinition: List[Parameter] = {
    modelInstance.getSignature.getInputDefinition
      .map(extractNussknackerSignature)
  }

  override def returnType: typing.TypingResult = Typed[Double]
}

object PrinzEnricher {

  private def extractNussknackerSignature(input: (SignatureName, SignatureType)): Parameter =
    Parameter[Double](input._1.name)
}
