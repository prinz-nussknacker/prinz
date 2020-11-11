package pl.touk.nussknacker.prinz.enrichers

import javax.validation.constraints.NotBlank
import pl.touk.nussknacker.engine.api.{MetaData, MethodToInvoke, ParamName, Service}

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class IrisSpeciesEnricher extends Service {

  @MethodToInvoke
  def invoke(@ParamName("sepalLength") @NotBlank sepalLength: Float,
             @ParamName("sepalWidth") @NotBlank sepalWidth: Float,
             @ParamName("petalLength") @NotBlank petalLength: Float,
             @ParamName("petalWidth") @NotBlank petalWidth: Float)
            (implicit ec: ExecutionContext, metaData: MetaData): Future[String] = {
    Future { "setosa" }
  }
}
