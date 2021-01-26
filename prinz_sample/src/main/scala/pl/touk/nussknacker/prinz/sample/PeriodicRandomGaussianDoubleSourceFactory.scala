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
import scala.math.sqrt
import scala.util.Random

object PeriodicRandomGaussianDoubleSourceFactory extends PeriodicRandomGaussianDoubleSourceFactory(
  new LegacyTimestampWatermarkHandler(new MapAscendingTimestampExtractor(MapAscendingTimestampExtractor.DefaultTimestampField)))

class PeriodicRandomGaussianDoubleSourceFactory(timestampAssigner: TimestampWatermarkHandler[Double]) extends FlinkSourceFactory[Double]  {

  @MethodToInvoke
  def create(@ParamName("period") period: Duration,
             // TODO: @DefaultValue(0) instead of nullable
             @ParamName("mean") @Nullable nullableMean: Double,
             // TODO: @DefaultValue(1) instead of nullable
             @ParamName("variance") @Nullable @Min(0) nullableVariance: Double,
             // TODO: @DefaultValue(1) instead of nullable
             @ParamName("count") @Nullable @Min(1) nullableCount: Integer): Source[_] = {
    new FlinkSource[Double] with ReturningType {

      override def typeInformation: TypeInformation[Double] = implicitly[TypeInformation[Double]]

      override def sourceStream(env: StreamExecutionEnvironment, flinkNodeContext: FlinkCustomNodeContext): DataStream[Double] = {
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

        //case class GaussianDistribution(myInt: Int, myString: String)
        val mean: Double = Option(nullableMean).map(_.toDouble).getOrElse(0)
        val variance: Double = Option(nullableVariance).map(_.toDouble).getOrElse(1)

        val count = Option(nullableCount).map(_.toInt).getOrElse(1)
        val processId = flinkNodeContext.metaData.id
        val stream = env
          .addSource(new PeriodicFunction(period))
          .map(_ => Context(processId))
          //.map(flinkNodeContext.lazyParameterHelper.lazyMapFunction(mean, variance))
          .flatMap { v =>
            1.to(count).map(_ => {
              val stdev = sqrt(variance)
              (Random.nextGaussian() * stdev) + mean
            })
          }

        timestampAssigner.assignTimestampAndWatermarks(stream)
      }

      override def timestampAssignerForTest: Option[TimestampWatermarkHandler[Double]] = Some(timestampAssigner)

      override val returnType: typing.TypingResult = typing.Typed[Double]

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
class MapAscendingTimestampExtractor(timestampField: String) extends AscendingTimestampExtractor[Double] {
  override def extractAscendingTimestamp(element: Double): Long = {
    System.currentTimeMillis()
  }
}

object MapAscendingTimestampExtractor {
  val DefaultTimestampField = "timestamp"
}
