package pl.touk.nussknacker.prinz.enrichers

import pl.touk.nussknacker.engine.api.definition.{NotBlankParameter, Parameter, ServiceWithExplicitMethod}
import pl.touk.nussknacker.engine.api.test.InvocationCollectors
import pl.touk.nussknacker.engine.api.typed.typing
import pl.touk.nussknacker.engine.api.{ContextId, MetaData}
import pl.touk.nussknacker.prinz.enrichers.PrinzEnricher.toNussknackerParameter
import pl.touk.nussknacker.prinz.model.{Model, SignatureField}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

case class PrinzEnricher(private val model: Model) extends ServiceWithExplicitMethod {

  private val modelInstance = model.toModelInstance

  private val inputNames = modelInstance
    .getSignature
    .getInputList
    .map(_.signatureName.name)

  override def invokeService(params: List[AnyRef])
                            (implicit ec: ExecutionContext, collector: InvocationCollectors.ServiceInvocationCollector,
                             metaData: MetaData, contextId: ContextId): Future[AnyRef] = {
    // TODO map params list and collect them according to columns order
    modelInstance.run(inputNames, List())
  }

  override def parameterDefinition: List[Parameter] = {
    modelInstance.getSignature.getInputList
      .map(toNussknackerParameter)
  }

  override def returnType: typing.TypingResult = {
    val output = modelInstance.getSignature.getOutputList
    if (output.size != 1) {
      throw new IllegalStateException("Model output signature supports only single value (for now)")
    }
    output.head.signatureType.typingResult
  }
}

object PrinzEnricher {

  private def toNussknackerParameter(input: SignatureField): Parameter =
    NotBlankParameter(input.signatureName.name, input.signatureType.typingResult)
}
