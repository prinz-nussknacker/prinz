package pl.touk.nussknacker.prinz.sample

import java.time.Duration
import java.{util => jul}

import javax.annotation.Nullable
import javax.validation.constraints.Min
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala._ // scalastyle:ignore
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import pl.touk.nussknacker.engine.api.{Context, MethodToInvoke, ParamName}
import pl.touk.nussknacker.engine.api.process.Source
import pl.touk.nussknacker.engine.api.typed.{ReturningType, typing}
import pl.touk.nussknacker.engine.flink.api.process.{FlinkCustomNodeContext, FlinkSource, FlinkSourceFactory}
import pl.touk.nussknacker.engine.flink.api.timestampwatermark.{LegacyTimestampWatermarkHandler, TimestampWatermarkHandler}

import scala.annotation.nowarn
import scala.math.sqrt
import scala.util.Random

object PeriodicRandomGaussianDoubleSourceFactory extends PeriodicRandomGaussianDoubleSourceFactory(
  new LegacyTimestampWatermarkHandler(new MapAscendingTimestampExtractor(MapAscendingTimestampExtractor.DefaultTimestampField)))

class PeriodicRandomGaussianDoubleSourceFactory(timestampAssigner: TimestampWatermarkHandler[Double]) extends FlinkSourceFactory[Double]  {

  @MethodToInvoke
  def create(@ParamName("period") period: Duration,
             @ParamName("mean") @Nullable nullableMean: Double,
             @ParamName("variance") @Nullable @Min(0) nullableVariance: Double,
             @ParamName("count") @Nullable @Min(1) nullableCount: Integer): Source[_] = {
    new FlinkSource[Double] with ReturningType {

      override def typeInformation: TypeInformation[Double] = implicitly[TypeInformation[Double]]

      override def sourceStream(env: StreamExecutionEnvironment, flinkNodeContext: FlinkCustomNodeContext): DataStream[Double] = {
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

        val mean: Double = Option(nullableMean).getOrElse(0)
        val variance: Double = Option(nullableVariance).getOrElse(1)
        val count: Integer = Option(nullableCount).getOrElse(1)
        val processId = flinkNodeContext.metaData.id
        val stream = env
          .addSource(new PeriodicFunction(period))
          .map(_ => Context(processId))
          .flatMap { _ =>
            1.to(count).map(_ => {
              val stdDev = sqrt(variance)
              (Random.nextGaussian() * stdDev) + mean
            })
          }

        timestampAssigner.assignTimestampAndWatermarks(stream)
      }

      override def timestampAssignerForTest: Option[TimestampWatermarkHandler[Double]] = Some(timestampAssigner)

      override val returnType: typing.TypingResult = typing.Typed[Double]

    }
  }

}

private class PeriodicFunction(duration: Duration) extends SourceFunction[Unit] {

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
private class MapAscendingTimestampExtractor(timestampField: String) extends AscendingTimestampExtractor[Double] {
  override def extractAscendingTimestamp(element: Double): Long = {
    System.currentTimeMillis()
  }
}

object MapAscendingTimestampExtractor {
  val DefaultTimestampField = "timestamp"
}
