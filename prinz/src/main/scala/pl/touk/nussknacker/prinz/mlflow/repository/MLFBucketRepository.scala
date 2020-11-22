package pl.touk.nussknacker.prinz.mlflow.repository

import java.io.InputStream

import pl.touk.nussknacker.prinz.mlflow.MlflowConstants.{S3_ACCESS_KEY, S3_SECRET_KEY, S3_URL}
import pl.touk.nussknacker.prinz.util.amazon.S3Client

class MLFBucketRepository(val bucketName: String) {

  private val s3Client = S3Client(S3_URL, S3_ACCESS_KEY, S3_SECRET_KEY)

  def getMLModelFile(experimentId: Int, runId: String): InputStream = s3Client
    .downloadFile(bucketName, s"$experimentId/$runId/artifacts/model/MLmodel")
}

object MLFBucketRepository {

  def apply(bucketName: String): MLFBucketRepository = new MLFBucketRepository(bucketName)
}
