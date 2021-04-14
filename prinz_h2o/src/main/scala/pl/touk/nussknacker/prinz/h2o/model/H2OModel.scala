package pl.touk.nussknacker.prinz.h2o.model

import hex.genmodel.GenModel
import hex.genmodel.algos.deeplearning.DeeplearningMojoModel
import hex.genmodel.easy.EasyPredictModelWrapper
import pl.touk.nussknacker.prinz.model.{Model, ModelInstance, ModelName, ModelVersion}

class H2OModel extends Model {
    //TODO placeholder model - we should create this model based on some config
    private val genModel: GenModel = new DeeplearningMojoModel(Array(), Array(), "")
    //TODO similarly to evaluator in PMMLModel we can use this wrapper to extract signature and score the model in ModelInstance
    val modelWrapper: EasyPredictModelWrapper = new EasyPredictModelWrapper(genModel)

    override def getName: ModelName = ???

    override def getVersion: ModelVersion = ???

    override def toModelInstance: ModelInstance = ???
}
