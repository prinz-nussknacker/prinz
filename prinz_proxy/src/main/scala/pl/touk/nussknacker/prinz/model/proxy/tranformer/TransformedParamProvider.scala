package pl.touk.nussknacker.prinz.model.proxy.tranformer

import pl.touk.nussknacker.prinz.model.ModelInstance.ModelInputData

import scala.concurrent.Future

trait TransformedParamProvider {

  def transformInputData(originalParameters: ModelInputData): Future[ModelInputData]
}
