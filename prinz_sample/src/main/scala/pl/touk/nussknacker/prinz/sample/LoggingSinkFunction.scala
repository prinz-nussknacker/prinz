package pl.touk.nussknacker.prinz.sample

import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.streaming.api.functions.sink.SinkFunction

object LoggingSinkFunction extends SinkFunction[AnyRef] with LazyLogging {
  override def invoke(value: AnyRef, context: SinkFunction.Context): Unit = {
    val loggedMessage = s"$value"
    logger.info(loggedMessage)
  }
}
