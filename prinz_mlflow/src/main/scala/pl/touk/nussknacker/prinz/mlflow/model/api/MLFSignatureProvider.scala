package pl.touk.nussknacker.prinz.mlflow.model.api

import com.typesafe.scalalogging.LazyLogging
import io.circe.Decoder
import io.circe.parser.decode
import io.circe.yaml.parser.{parse => parseYaml}
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.converter.MLFSignatureInterpreter
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.{MLFJsonMLModel, MLFRestRunId, MLFYamlInputDefinition, MLFYamlModelDefinition, MLFYamlOutputDefinition}
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.{MLFBucketClient, MLFBucketClientConfig, MLFRestClient, MLFRestClientConfig}
import pl.touk.nussknacker.prinz.model.SignatureProvider.indexedOutputName
import pl.touk.nussknacker.prinz.model.{Model, ModelSignature, SignatureField, SignatureName, SignatureProvider, SignatureType}

import java.io.{InputStream, InputStreamReader, Reader}


case class MLFSignatureProvider(private val config: MLFConfig)
  extends SignatureProvider with LazyLogging {

  private val bucketClient = MLFBucketClient(MLFBucketClientConfig.fromMLFConfig(config))

  private val restClient = MLFRestClient(MLFRestClientConfig.fromMLFConfig(config))

  override def provideSignature(model: Model): Option[ModelSignature] = model match {
    case model: MLFRegisteredModel => getLatestModelVersionArtifactLocation(model.getVersion.runId)
      .map(bucketClient.getMLModelFile)
      .flatMap(extractDefinitionAndCloseStream)
      .map(definitionToSignature)
    case _ => throw new IllegalArgumentException("MLFSignatureInterpreter can interpret only MLFRegisteredModels")
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

  private def definitionToSignature(definition: MLFYamlModelDefinition): ModelSignature = {
    val signatureInputs = definition.inputs.map { i =>
      SignatureField(
        SignatureName(i.name),
        SignatureType(MLFSignatureInterpreter.fromMLFDataType(i.`type`)))
    }
    val signatureOutputs = for ((o, index) <- definition.output.zipWithIndex) yield
      SignatureField(
        indexedOutputName(index),
        SignatureType(MLFSignatureInterpreter.fromMLFDataType(o.`type`))
      )
    val signature = ModelSignature(signatureInputs, signatureOutputs)
    logger.info("Downloaded mlflow signature: {}", signature)
    signature
  }
}
