package pl.touk.nussknacker.prinz.mlflow.converter

import com.typesafe.scalalogging.LazyLogging
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.auto.{exportDecoder, exportEncoder}
import io.circe.jawn.{decode, parse}
import io.circe.syntax.EncoderOps
import pl.touk.nussknacker.engine.api.typed.typing.{Typed, TypingResult}
import pl.touk.nussknacker.prinz.model.{ModelSignature, SignatureName}
import pl.touk.nussknacker.prinz.util.collection.immutable.VectorMultimap

object MLFDataConverter extends LazyLogging {

  implicit private val encodeDecimalOrString: Encoder[Either[BigDecimal, String]] =
    Encoder.instance(_.fold(_.asJson, _.asJson))

  implicit private val decodeDecimalOrString: Decoder[Either[BigDecimal, String]] =
    Decoder[BigDecimal].map(Left(_)).or(Decoder[String].map(Right(_)))

  case class Dataframe(columns: List[String], data: List[List[MLFDataTypeWrapper]]) {

    def toList: List[(String, MLFDataTypeWrapper)] =
      (for (i <- columns.indices; record <- data) yield (columns(i), record(i))).toList
  }

  def toJsonString(multimap: VectorMultimap[String, AnyRef], signature: ModelSignature): String = {
    if (!isMultimapConvertible(multimap)) {
      throw new IllegalArgumentException("Invalid multimap data given for mlflow data conversion")
    }

    val columns = multimap.keys.toList
    val data = multimap
      .values
      .flatMap(_.zipWithIndex)
      .map { case (v, idx) => (MLFDataTypeWrapper(signature, columns, idx, v), idx) }
      .groupBy { case (_, idx) => idx }
      .map { case (_, v) => v.map(_._1).toList }
      .toList

    val dataframe = Dataframe(columns, data)
    dataframe.asJson.toString()
  }

  private def isDataframeConvertible(dataframe: Dataframe): Boolean =
    !dataframe.data.exists(record => record.length != dataframe.columns.length)

  private def isMultimapConvertible(multimap: VectorMultimap[String, AnyRef]): Boolean =
    multimap.values.map(_.size).toSet.size <= 1
}
