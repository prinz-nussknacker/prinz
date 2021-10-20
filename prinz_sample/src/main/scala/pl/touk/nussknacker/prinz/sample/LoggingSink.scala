package pl.touk.nussknacker.prinz.sample

import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction.Context
import pl.touk.nussknacker.engine.flink.api.process.BasicFlinkSink

object LoggingSink extends BasicFlinkSink with LazyLogging {

  override def testDataOutput: Option[Any => String] =
    Some(value => s"$value")

  override def toFlinkFunction: SinkFunction[Any] = new SinkFunction[Any] with Serializable {
    override def invoke(value: Any, context: Context): Unit = {
      val loggedMessage = s"$value"
      logger.info(loggedMessage)
    }
  }
}
