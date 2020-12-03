package pl.touk.nussknacker.prinz.mlflow.model.rest.client

import java.io.InputStream

import pl.touk.nussknacker.prinz.mlflow.MlflowConstants.{S3_ACCESS_KEY, S3_MODEL_REL_PATH, S3_SECRET_KEY, S3_URL}
import pl.touk.nussknacker.prinz.util.amazon.S3Client

class MLFBucketRestClient(val bucketName: String) {

  private val s3Client = S3Client(S3_URL, S3_ACCESS_KEY, S3_SECRET_KEY)

  def getMLModelFile(experimentId: Int, runId: String): InputStream = s3Client
    .downloadFile(bucketName, s"$experimentId/$runId$S3_MODEL_REL_PATH")
}
