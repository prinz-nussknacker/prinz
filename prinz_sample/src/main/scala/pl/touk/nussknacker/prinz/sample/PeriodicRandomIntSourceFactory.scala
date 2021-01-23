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

object PeriodicRandomIntSourceFactory extends PeriodicRandomIntSourceFactory(
  new LegacyTimestampWatermarkHandler(new MapAscendingTimestampExtractor(MapAscendingTimestampExtractor.DefaultTimestampField)))

class PeriodicRandomIntSourceFactory(timestampAssigner: TimestampWatermarkHandler[Int]) extends FlinkSourceFactory[Int]  {

  @MethodToInvoke
  def create(@ParamName("period") period: Duration,
             // TODO: @DefaultValue(1) instead of nullable
             @ParamName("count") @Nullable @Min(1) nullableCount: Integer): Source[_] = {
    new FlinkSource[Int] with ReturningType {

      override def typeInformation: TypeInformation[Int] = implicitly[TypeInformation[Int]]

      override def sourceStream(env: StreamExecutionEnvironment, flinkNodeContext: FlinkCustomNodeContext): DataStream[Int] = {
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

        val count = Option(nullableCount).map(_.toInt).getOrElse(1)
        val processId = flinkNodeContext.metaData.id
        val stream = env
          .addSource(new PeriodicFunction(period))
          .map(_ => Context(processId))
          .flatMap { _ =>
            1.to(count).map(_ => 800)
          }

        timestampAssigner.assignTimestampAndWatermarks(stream)
      }

      override def timestampAssignerForTest: Option[TimestampWatermarkHandler[Int]] = Some(timestampAssigner)

      override val returnType: typing.TypingResult = typing.Typed[Int]

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
class MapAscendingTimestampExtractor(timestampField: String) extends AscendingTimestampExtractor[Int] {
  override def extractAscendingTimestamp(element: Int): Long = {
    System.currentTimeMillis()
  }
}

object MapAscendingTimestampExtractor {
  val DefaultTimestampField = "timestamp"
}
