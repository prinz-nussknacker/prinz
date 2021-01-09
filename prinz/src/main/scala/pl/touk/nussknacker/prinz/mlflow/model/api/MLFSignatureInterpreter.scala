package pl.touk.nussknacker.prinz.mlflow.model.api

import io.circe.Decoder
import io.circe.parser.decode
import io.circe.yaml.parser.{parse => parseYaml}
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.{MLFJsonMLModel, MLFRestRunId, MLFYamlInputDefinition, MLFYamlModelDefinition, MLFYamlOutputDefinition}
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.{MLFBucketClient, MLFBucketClientConfig, MLFRestClient, MLFRestClientConfig}
import pl.touk.nussknacker.prinz.model.{Model, ModelSignature, SignatureInterpreter, SignatureName, SignatureType}

import java.io.{InputStream, InputStreamReader, Reader}

case class MLFSignatureInterpreter(private val config: MLFConfig)
  extends SignatureInterpreter {

  private val bucketClient = MLFBucketClient(MLFBucketClientConfig.fromMLFConfig(config))

  private val restClient = MLFRestClient(MLFRestClientConfig.fromMLFConfig(config))

  override def downloadSignature(model: Model): Option[ModelSignature] = model match {
    case model: MLFRegisteredModel => getLatestModelVersionArtifactLocation(model.getVersion.runId)
      .map(bucketClient.getMLModelFile)
      .flatMap(extractDefinitionAndCloseStream)
      .map(definitionToSignature)
    case _ => throw new IllegalArgumentException("MLFSignatureInterpreter can interpret only MLFRegisteredModels")
  }

  def fromMLFDataType(typeName: String): TypingResult = typeName match {
    case "boolean" => Typed[Boolean]
    case "integer" => Typed[Int]
    case "long" => Typed[Long]
    case "float" => Typed[Float]
    case "double" => Typed[Double]
    case "string" => Typed[String]
    case "binary" => Typed[Array[Byte]]
  }

  private def extractDefinitionAndCloseStream(stream: InputStream): Option[MLFYamlModelDefinition] = {
    val definition = extractDefinition(new InputStreamReader(stream))
    stream.close()
    definition
  }

  private def getLatestModelVersionArtifactLocation(runId: String): Option[String] =
    restClient.getRunInfo(MLFRestRunId(runId))
      .toOption
      .map(_.info.artifact_uri)

  private def extractDefinition(yamlFile: Reader): Option[MLFYamlModelDefinition] = for {
    model <- parseYaml(yamlFile).flatMap(_.as[MLFJsonMLModel]).toOption
    inputs <- extractDefinition[MLFYamlInputDefinition](model.signature.inputs)
    outputs <- extractDefinition[MLFYamlOutputDefinition](model.signature.outputs)
  } yield MLFYamlModelDefinition(inputs, outputs)

  private def extractDefinition[A](input: String)(implicit decoder: Decoder[List[A]]): Option[List[A]] =
    decode(input).toOption

  private def definitionToSignature(definition: MLFYamlModelDefinition): ModelSignature =
    ModelSignature(
      definition.inputs.map(i => (SignatureName(i.name), SignatureType(fromMLFDataType(i.`type`)))),
      definition.output.map(o => SignatureType(fromMLFDataType(o.`type`)))
    )
}
