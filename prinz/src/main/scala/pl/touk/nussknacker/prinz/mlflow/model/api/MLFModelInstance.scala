package pl.touk.nussknacker.prinz.mlflow.model.api

import java.io.{InputStreamReader, Reader}
import java.net.URL

import io.circe.Decoder
import io.circe.parser.decode
import io.circe.yaml.parser.{parse => parseYaml}
import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.api.MLFModelInstance.downloadSignature
import pl.touk.nussknacker.prinz.mlflow.model.rest.api.{MLFJsonMLModel, MLFYamlInputDefinition,
  MLFYamlModelDefinition, MLFYamlOutputDefinition, RestMLFInvokeBody}
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.{MLFBucketRestClient, MLFInvokeRestClient}
import pl.touk.nussknacker.prinz.model.{ModelInstance, ModelRunException, ModelSignature, SignatureName, SignatureType}

case class MLFModelInstance(runUrl: URL, model: MLFRegisteredModel) extends ModelInstance {

  private val restClient = new MLFInvokeRestClient(runUrl.toString, model)

  override def run(columns: List[String], data: List[List[Double]]): Either[ModelRunException, Double] =
    restClient.invoke(RestMLFInvokeBody(columns, data))
      .left.map(new ModelRunException(_))

  override def getSignature: ModelSignature = downloadSignature(MLFConfig.experimentId, model.getVersion.runId) match {
    case Some(value) => value
    case None => throw MLFSignatureNotFoundException(model)
  }
}

object MLFModelInstance {

  private def downloadSignature(experimentId: Int, runId: String): Option[ModelSignature] = {
    val s3Client = new MLFBucketRestClient(MLFConfig.s3BucketName)
    val stream = s3Client.getMLModelFile(experimentId, runId)
    extractDefinition(new InputStreamReader(stream))
      .map(definitionToSignature)
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
