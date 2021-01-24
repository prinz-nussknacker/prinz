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

object PeriodicRandomFloatSourceFactory extends PeriodicRandomFloatSourceFactory(
  new LegacyTimestampWatermarkHandler(new MapAscendingTimestampExtractor(MapAscendingTimestampExtractor.DefaultTimestampField)))

class PeriodicRandomFloatSourceFactory(timestampAssigner: TimestampWatermarkHandler[Float]) extends FlinkSourceFactory[Float]  {

  @MethodToInvoke
  def create(@ParamName("period") period: Duration,
             // TODO: @DefaultValue(1) instead of nullable
             @ParamName("count") @Nullable @Min(1) nullableCount: Integer): Source[_] = {
    new FlinkSource[Float] with ReturningType {

      override def typeInformation: TypeInformation[Float] = implicitly[TypeInformation[Float]]

      override def sourceStream(env: StreamExecutionEnvironment, flinkNodeContext: FlinkCustomNodeContext): DataStream[Float] = {
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

        val count = Option(nullableCount).map(_.toInt).getOrElse(1)
        val processId = flinkNodeContext.metaData.id
        val stream = env
          .addSource(new PeriodicFunction(period))
          .map(_ => Context(processId))
          .flatMap { _ =>
            1.to(count).map(_ => 800.floatValue())
          }

        timestampAssigner.assignTimestampAndWatermarks(stream)
      }

      override def timestampAssignerForTest: Option[TimestampWatermarkHandler[Float]] = Some(timestampAssigner)

      override val returnType: typing.TypingResult = typing.Typed[Float]

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
class MapAscendingTimestampExtractor(timestampField: String) extends AscendingTimestampExtractor[Float] {
  override def extractAscendingTimestamp(element: Float): Long = {
    System.currentTimeMillis()
  }
}

object MapAscendingTimestampExtractor {
  val DefaultTimestampField = "timestamp"
}
