package pl.touk.nussknacker.prinz.sample

import java.time.Duration
import java.{util => jul}

import javax.annotation.Nullable
import javax.validation.constraints.Min
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import pl.touk.nussknacker.engine.api._
import pl.touk.nussknacker.engine.api.process.Source
import pl.touk.nussknacker.engine.api.typed.{ReturningType, typing}
import pl.touk.nussknacker.engine.flink.api.process.{FlinkCustomNodeContext, FlinkSource, FlinkSourceFactory}
import pl.touk.nussknacker.engine.flink.api.timestampwatermark.{LegacyTimestampWatermarkHandler, TimestampWatermarkHandler}

import scala.annotation.nowarn
import scala.collection.JavaConverters._

object PeriodicRandomEntrySourceFactory extends PeriodicRandomEntrySourceFactory(
  new LegacyTimestampWatermarkHandler(new MapAscendingTimestampExtractor(MapAscendingTimestampExtractor.DefaultTimestampField)))

class PeriodicRandomEntrySourceFactory(timestampAssigner: TimestampWatermarkHandler[Array[Any]]) extends FlinkSourceFactory[Array[Any]]  {

  @MethodToInvoke
  def create(@ParamName("period") period: Duration,
             // TODO: @DefaultValue(1) instead of nullable
             @ParamName("count") @Nullable @Min(1) nullableCount: Integer): Source[_] = {
    new FlinkSource[Array[Any]] with ReturningType {

      override def typeInformation: TypeInformation[Array[Any]] = implicitly[TypeInformation[Array[Any]]]

      override def sourceStream(env: StreamExecutionEnvironment, flinkNodeContext: FlinkCustomNodeContext): DataStream[Array[Any]] = {
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

        val count = Option(nullableCount).map(_.toInt).getOrElse(1)
        val processId = flinkNodeContext.metaData.id
        val stream = env
          .addSource(new PeriodicFunction(period))
          .map(_ => Context(processId))
          .flatMap { _ =>
            1.to(count).map(_ => Array("4", "M", "es_transportation", 800))
          }

        timestampAssigner.assignTimestampAndWatermarks(stream)
      }

      override def timestampAssignerForTest: Option[TimestampWatermarkHandler[Array[Any]]] = Some(timestampAssigner)

      override val returnType: typing.TypingResult = typing.Typed[Array[Any]]

    }
  }

}

class PeriodicFunction(duration: Duration) extends SourceFunction[Unit] {

  @volatile private var isRunning = true

  override def run(ctx: SourceFunction.SourceContext[Unit]): Unit = {
    while (isRunning) {
      ctx.collect(Unit)
      Thread.sleep(duration.toMillis)
    }
  }

  override def cancel(): Unit = {
    isRunning = false
  }

}

@nowarn("deprecated")
class MapAscendingTimestampExtractor(timestampField: String) extends AscendingTimestampExtractor[Array[Any]] {
  override def extractAscendingTimestamp(element: Array[Any]): Long = {
    System.currentTimeMillis()
  }
}

object MapAscendingTimestampExtractor {
  val DefaultTimestampField = "timestamp"
}
