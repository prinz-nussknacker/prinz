package pl.touk.nussknacker.prinz.mlflow.model.api

import io.circe.Decoder
import io.circe.parser.decode
import io.circe.yaml.parser.{parse => parseYaml}
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.{MLFJsonMLModel, MLFRestRunId, MLFYamlInputDefinition, MLFYamlModelDefinition, MLFYamlOutputDefinition}
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.{MLFBucketRestClient, MLFRestClient}
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureInterpreter, SignatureName, SignatureType}

import java.io.{InputStream, InputStreamReader, Reader}

object MLFSignatureInterpreter extends SignatureInterpreter {

  private val s3Client = new MLFBucketRestClient(MLFConfig.s3BucketName)

  override def downloadSignature(runId: String): Option[ModelSignature] =
    getLatestModelVersionArtifactLocation(runId)
      .map(s3Client.getMLModelFile)
      .flatMap(extractDefinitionAndCloseStream)
      .map(definitionToSignature)

  private def extractDefinitionAndCloseStream(stream: InputStream): Option[MLFYamlModelDefinition] = {
    val definition = extractDefinition(new InputStreamReader(stream))
    stream.close()
    definition
  }

  private def getLatestModelVersionArtifactLocation(runId: String): Option[String] = {
    val client = MLFRestClient(MLFConfig.serverUrl)
    client.getRunInfo(MLFRestRunId(runId))
      .toOption
      .map(_.info.artifact_uri)
  }

  private def extractDefinition(yamlFile: Reader): Option[MLFYamlModelDefinition] = for {
    model <- parseYaml(yamlFile).flatMap(_.as[MLFJsonMLModel]).toOption
    inputs <- extractDefinition[MLFYamlInputDefinition](model.signature.inputs)
    outputs <- extractDefinition[MLFYamlOutputDefinition](model.signature.outputs)
  } yield MLFYamlModelDefinition(inputs, outputs)

  private def extractDefinition[A](input: String)(implicit decoder: Decoder[List[A]]): Option[List[A]] =
    decode(input).toOption

  private def definitionToSignature(definition: MLFYamlModelDefinition): ModelSignature =
    ModelSignature(
      definition.inputs.map(i => (SignatureName(i.name), SignatureType(i.`type`))),
      definition.output.map(o => SignatureType(o.`type`))
    )
}
