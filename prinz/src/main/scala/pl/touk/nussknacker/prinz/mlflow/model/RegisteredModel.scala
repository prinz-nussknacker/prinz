package pl.touk.nussknacker.prinz.mlflow.model

import java.io.{InputStreamReader, Reader}
import java.net.URL

import pl.touk.nussknacker.prinz.mlflow.MlflowConstants.{BUCKET_NAME, EXPERIMENT_ID}
import pl.touk.nussknacker.prinz.mlflow.repository.MLFBucketRepository
import pl.touk.nussknacker.prinz.model.{Model, ModelName, ModelSignature, ModelVersion, SignatureType}
import io.circe.generic.auto.exportDecoder
import io.circe.yaml.parser.{parse => parseYaml}
import org.json4s.{DefaultFormats, stream2JsonInput}
import org.json4s.native.JsonMethods.{parse => parseJson}
import pl.touk.nussknacker.prinz.mlflow.model.RegisteredModelVersion.downloadSignature

case class ModelVersionTag(key: String, value: String)

case class RegisteredModelTag(key: String, value: String)

case class RegisteredModelVersion(name: String, version: String, creation_timestamp: String, last_updated_timestamp: String,
                                  current_stage: String, source: String,
                                  run_id: String, status: String, tags: List[ModelVersionTag]) extends ModelVersion {

  override def getModelInstance: Option[MLFModelInstance] =
    downloadSignature(EXPERIMENT_ID, run_id)
      .map(new MLFModelInstance(new URL("http://0.0.0.0:1234"), _))
}

case class RegisteredModel(name: String, creation_timestamp: String, last_updated_timestamp: String,
                           latest_versions: List[RegisteredModelVersion], tags: List[RegisteredModelTag]) extends Model {

  override def getName: ModelName = ModelName(name)

  override def getLatestVersion: RegisteredModelVersion = latest_versions.head

  override def getVersions: List[RegisteredModelVersion] = latest_versions
}

object RegisteredModelVersion {

  private def downloadSignature(experimentId: Int, runId: String): Option[ModelSignature] = {
    val bucketRepository = MLFBucketRepository(BUCKET_NAME)
    val stream = bucketRepository.getMLModelFile(experimentId, runId)
    extractDefinition(new InputStreamReader(stream))
      .map(definitionToSignature)
  }

  private def extractDefinition(yamlFile: Reader): Option[MLFModelDefinition] = for {
    model <- parseYaml(yamlFile).flatMap(_.as[MLFMLModel]).toOption
    inputs = extractDefinition[MLF](model.signature.inputs)
    outputs = extractDefinition[MLFOutputDefinition](model.signature.outputs)
  } yield MLFModelDefinition(inputs, outputs)

  private def extractDefinition[A: Manifest](input: String): List[A] = {
    implicit val formats: DefaultFormats = DefaultFormats
    parseJson(input).extract[List[A]]
  }

  private def definitionToSignature(definition: MLFModelDefinition): ModelSignature =
    ModelSignature(
      definition.inputs.map(i => (i.name, SignatureType(i.`type`))),
      definition.output.map(o => SignatureType(o.`type`))
    )
}

private case class MLFJsonSignature(inputs: String, outputs: String)
private case class MLFMLModel(signature: MLFJsonSignature)

private case class MLF(name: String, `type`: String)
private case class MLFOutputDefinition(`type`: String)
private case class MLFModelDefinition(inputs: List[MLF], output: List[MLFOutputDefinition])