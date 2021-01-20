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

  case class MLFType(val typ: String, val v: AnyRef)

  implicit val encodeMLF: Encoder[MLFType] = new Encoder[MLFType] {
    override def apply(a: MLFType): Json = a.typ match {
      case "integer" => Json.fromInt(a.v.asInstanceOf[Integer])
      case "double" => Json.fromDoubleOrNull(a.v.asInstanceOf[java.lang.Double])
    }
  }

  implicit private val encodeDecimalOrString: Encoder[Either[BigDecimal, String]] =
    Encoder.instance(_.fold(_.asJson, _.asJson))

  implicit private val decodeDecimalOrString: Decoder[Either[BigDecimal, String]] =
    Decoder[BigDecimal].map(Left(_)).or(Decoder[String].map(Right(_)))

  case class Dataframe(columns: List[String], data: List[List[MLFType]]) {

    def toList: List[(String, MLFType)] =
      (for (i <- columns.indices; record <- data) yield (columns(i), record(i))).toList
  }

  def extractType(signature: ModelSignature, columns: List[String], index: Int): String = {
    //signature.getInputValueType(SignatureName(columns(index))).get.typingResult.toString
    "double"
  }

  def toJsonString(multimap: VectorMultimap[String, AnyRef], signature: ModelSignature): String = {
    if (!isMultimapConvertible(multimap)) {
      throw new IllegalArgumentException("Invalid multimap")
    }

    val columns = multimap.keys.toList
    val data = multimap
      .values
      .flatMap(_.zipWithIndex) //(val, idx)
      .map { case (v, idx) => (MLFType(extractType(signature, columns, idx), v), idx)}
      .groupBy { case (num, idx) => idx }
      .map { case (k, v) => v.map(_._1).toList }
      .toList

    val test = Dataframe(columns, data).asJson.toString()
    logger.info(test)
    test
  }

  private def isDataframeConvertible(dataframe: Dataframe): Boolean =
    !dataframe.data.exists(record => record.length != dataframe.columns.length)

  private def isMultimapConvertible(multimap: VectorMultimap[String, AnyRef]): Boolean =
    multimap.values.map(_.size).toSet.size <= 1
}
