package pl.touk.nussknacker.prinz.util.converter.mlflow

import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import io.circe.jawn.{parse, decode}
import io.circe.syntax.EncoderOps
import pl.touk.nussknacker.prinz.util.collection.immutable.SetMultimap

class MlflowDataConverter {

  implicit val encodeDecimalOrString: Encoder[Either[BigDecimal, String]] =
    Encoder.instance(_.fold(_.asJson, _.asJson))

  implicit val decodeDecimalOrString: Decoder[Either[BigDecimal, String]] =
    Decoder[BigDecimal].map(Left(_)).or(Decoder[String].map(Right(_)))

  case class Dataframe(columns: List[String], data: List[List[Either[BigDecimal, String]]]) {

    def toList: List[(String, Either[BigDecimal, String])] =
      (for (i <- columns.indices; record <- data) yield (columns(i), record(i))).toList
  }

  def toMultimap(data: String): SetMultimap[String, Either[BigDecimal, String]] = {
    val dataframe = decode[Dataframe](parse(data).getOrElse(Json.Null).noSpaces).
      getOrElse(throw new IllegalArgumentException("Invalid data"))

    if (!isDataframeConvertible(dataframe)) {
      throw new IllegalArgumentException("Invalid data")
    }

    val list = dataframe.toList
    SetMultimap.empty.addAll(list)
  }

  def toJsonString(multimap: SetMultimap[String, Either[BigDecimal, String]]): String = {
    if (!isMultimapConvertible(multimap)) {
      throw new IllegalArgumentException("Invalid multimap")
    }

    val columns = multimap.keys.toList
    val data = multimap.
      values.
      flatMap(_.zipWithIndex).
      groupBy{case (num, idx) => idx}.
      map{case (k, v) => v.map(_._1).toList}.
      toList

    Dataframe(columns, data).asJson.toString()
  }

  private def isDataframeConvertible(dataframe: Dataframe): Boolean =
    dataframe != null && !dataframe.data.exists(record => record.length != dataframe.columns.length)

  private def isMultimapConvertible(multimap: SetMultimap[String, Either[BigDecimal, String]]): Boolean =
    multimap != null && multimap.values.map(_.size).toSet.size <= 1
}

object MlflowDataConverter {

  def apply(): MlflowDataConverter = new MlflowDataConverter
}
