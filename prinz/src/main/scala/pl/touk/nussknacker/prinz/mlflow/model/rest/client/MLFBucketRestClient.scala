package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import java.io.InputStream

import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.mlflow.model.rest.client.MLFBucketRestClient.extractBucketRelativePath
import pl.touk.nussknacker.prinz.util.amazon.S3Client

class MLFBucketRestClient(val bucketName: String) {

  private val s3Client = S3Client(MLFConfig.s3Url, MLFConfig.s3AccessKey, MLFConfig.s3SecretKey)

  def getMLModelFile(artifactLocation: String): InputStream = s3Client
    .downloadFile(bucketName, extractBucketRelativePath(s"$artifactLocation${MLFConfig.s3ModelRelativePath}", bucketName))
}

object MLFBucketRestClient {

  private def extractBucketRelativePath(fullPath: String, bucketName: String): String = {
    val startIndex = fullPath.indexOf(bucketName) + bucketName.length
    fullPath.substring(startIndex)
  }
}
