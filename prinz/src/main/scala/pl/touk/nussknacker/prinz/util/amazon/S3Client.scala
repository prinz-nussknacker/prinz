package pl.touk.nussknacker.prinz.util.amazon

import java.io.InputStream
import java.net.URL

import io.minio.{GetObjectArgs, MinioClient}

class S3Client(val endpoint: URL, val accessKey: String, val secretKey: String) {

  private val client = MinioClient.builder()
    .endpoint(endpoint)
    .credentials(accessKey, secretKey)
    .build();

  def  downloadFile(bucket: String, objectPath: String): InputStream =
    client.getObject(GetObjectArgs.builder().bucket(bucket).`object`(objectPath).build())
}

object S3Client {

  def apply(endpoint: URL, accessKey: String, secretKey: String): S3Client = new S3Client(endpoint, accessKey, secretKey)
}