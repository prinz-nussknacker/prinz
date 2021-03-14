package pl.touk.nussknacker.prinz

import pl.touk.nussknacker.prinz.model.ModelInstance.ModelInputData
import pl.touk.nussknacker.prinz.model.ModelSignature
import pl.touk.nussknacker.prinz.model.proxy.tranformer.ModelInputTransformer

import scala.concurrent.Future

class TestH2IdTransformedParamProvider extends ModelInputTransformer with H2Database {

  override def transformInputData(originalParameters: ModelInputData): Future[ModelInputData] = {
    val ids = originalParameters.get("id").map(_.asInstanceOf[Int])

  }

  override def changeSignature(modelSignature: ModelSignature): ModelSignature = {

  }
}
