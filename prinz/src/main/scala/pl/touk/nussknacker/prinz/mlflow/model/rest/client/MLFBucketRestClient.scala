package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import java.io.InputStream

import pl.touk.nussknacker.prinz.mlflow.MLFConfig
import pl.touk.nussknacker.prinz.util.amazon.S3Client

class MLFBucketRestClient(val bucketName: String) {

  private val s3Client = S3Client(MLFConfig.s3Url, MLFConfig.s3AccessKey, MLFConfig.s3SecretKey)

  def getMLModelFile(experimentId: Int, runId: String): InputStream = s3Client
    .downloadFile(bucketName, s"$experimentId/$runId${MLFConfig.s3ModelRelativePath}")
}
