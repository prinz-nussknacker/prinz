package pl.touk.nussknacker.prinz

import pl.touk.nussknacker.prinz.model.ModelInstance.ModelInputData
import pl.touk.nussknacker.prinz.model.proxy.tranformer.TransformedParamProvider

import scala.concurrent.Future

class TestH2IdTransformedParamProvider extends TransformedParamProvider with H2Database {

  override def transformInputData(originalParameters: ModelInputData): Future[ModelInputData] = {
    val ids = originalParameters.get("id").map(_.asInstanceOf[Int])

  }
}
