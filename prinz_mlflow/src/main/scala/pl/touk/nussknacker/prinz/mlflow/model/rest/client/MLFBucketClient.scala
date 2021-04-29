package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import pl.touk.nussknacker.prinz.mlflow.MLFConfig

import java.io.InputStream
import java.net.URL
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFBucketClient.extractBucketRelativePath
import pl.touk.nussknacker.prinz.mlflow.util.amazon.S3Client

case class MLFBucketClient(private val config: MLFBucketClientConfig) {

  private val s3Client = S3Client(config.s3Url, config.s3AccessKey, config.s3SecretKey)

  def getMLModelFile(artifactLocation: String): InputStream = s3Client
    .downloadFile(config.bucketName,
      extractBucketRelativePath(s"$artifactLocation${config.s3ModelRelativePath}", config.bucketName))
}

object MLFBucketClient {

  private def extractBucketRelativePath(fullPath: String, bucketName: String): String = {
    val startIndex = fullPath.indexOf(bucketName) + bucketName.length
    fullPath.substring(startIndex)
  }
}

case class MLFBucketClientConfig(bucketName: String,
                                 s3ModelRelativePath: String,
                                 s3Url: URL,
                                 s3AccessKey: String, s3SecretKey: String)

object MLFBucketClientConfig {

  def fromMLFConfig(config: MLFConfig): MLFBucketClientConfig =
    MLFBucketClientConfig(config.s3BucketName, config.s3ModelRelativePath, config.s3Url, config.s3AccessKey, config.s3SecretKey)
}
