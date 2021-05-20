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
import pl.touk.nussknacker.prinz.model.SignatureProvider.{ProvideSignatureResult, indexedOutputName}
import pl.touk.nussknacker.prinz.model.{Model, ModelSignature, ModelSignatureLocationMetadata, SignatureField, SignatureName, SignatureProvider, SignatureType}

import java.io.{InputStream, InputStreamReader, Reader}


case class MLFSignatureProvider(private val config: MLFConfig)
  extends SignatureProvider with LazyLogging {

  private val bucketClient = MLFBucketClient(MLFBucketClientConfig.fromMLFConfig(config))

  private val restClient = MLFRestClient(MLFRestClientConfig.fromMLFConfig(config))

  override def provideSignature(modelSignatureLocationMetadata: ModelSignatureLocationMetadata): ProvideSignatureResult =
    modelSignatureLocationMetadata match {
      case metadata: MLFModelSignatureLocationMetadata => getLatestModelVersionArtifactLocation(metadata.version.runId)
        .map(bucketClient.getMLModelFile)
        .flatMap(extractDefinitionAndCloseStream)
        .map(definitionToSignature)
      case _: Any => Left(new IllegalArgumentException("MLFSignatureProvider can interpret only PMMLModels"))
    }

  private def extractDefinitionAndCloseStream(stream: InputStream): Either[Exception, MLFYamlModelDefinition] = try {
    extractDefinition(new InputStreamReader(stream))
  } finally {
    stream.close()
  }

  private def getLatestModelVersionArtifactLocation(runId: String): Either[Exception, String] =
    restClient.getRunInfo(MLFRestRunId(runId))
      .map(_.info.artifact_uri)

  private def extractDefinition(yamlFile: Reader): Either[Exception, MLFYamlModelDefinition] = {
    val definition = for {
      model <- parseYaml(yamlFile).flatMap(_.as[MLFJsonMLModel]).toOption
      inputs <- extractDefinition[MLFYamlInputDefinition](model.signature.inputs)
      outputs <- extractDefinition[MLFYamlOutputDefinition](model.signature.outputs)
    } yield MLFYamlModelDefinition(inputs, outputs)
    definition.toRight[Exception](new RuntimeException(s"Parse model signature exception: $yamlFile"))
  }

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
