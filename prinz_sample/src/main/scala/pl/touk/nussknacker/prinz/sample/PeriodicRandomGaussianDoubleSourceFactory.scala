package pl.touk.nussknacker.prinz.sample

import java.time.Duration
import javax.annotation.Nullable
import javax.validation.constraints.Min
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.functions.source.SourceFunction
import pl.touk.nussknacker.engine.api.{MethodToInvoke, ParamName}
import pl.touk.nussknacker.engine.api.process.{Source, SourceFactory}
import pl.touk.nussknacker.engine.flink.api.process.BasicFlinkSource
import pl.touk.nussknacker.engine.flink.api.timestampwatermark.TimestampWatermarkHandler

import scala.math.sqrt
import scala.util.Random

object PeriodicRandomGaussianDoubleSourceFactory extends SourceFactory {

  @MethodToInvoke
  def create(@ParamName("period") period: Duration,
             @ParamName("mean") @Nullable nullableMean: Double,
             @ParamName("variance") @Nullable @Min(0) nullableVariance: Double,
             @ParamName("count") @Nullable @Min(1) nullableCount: Integer): Source =
    new BasicFlinkSource[Double] {

      override def flinkSourceFunction: SourceFunction[Double] = {
        val mean = Option[Double](nullableMean).getOrElse(0.0)
        val variance = Option[Double](nullableVariance).getOrElse(1.0)
        new PeriodicGaussSourceFunction(period, mean, variance)
      }

      override def typeInformation: TypeInformation[Double] = implicitly[TypeInformation[Double]]

      override def timestampAssigner: Option[TimestampWatermarkHandler[Double]] = None
    }
}

private class PeriodicGaussSourceFunction(sleep: Duration, mean: Double, variance: Double) extends SourceFunction[Double] {

  @volatile private var isRunning = true

  override def run(ctx: SourceFunction.SourceContext[Double]): Unit = {
    while (isRunning) {
      val stdDev = sqrt(variance)
      val rnd = (Random.nextGaussian() * stdDev) + mean
      ctx.collect(rnd)
      Thread.sleep(sleep.toMillis)
    }
  }

  override def cancel(): Unit = {
    isRunning = false
  }
}
