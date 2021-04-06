package pl.touk.nussknacker.prinz.sample

import com.typesafe.scalalogging.LazyLogging
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction.Context
import pl.touk.nussknacker.engine.flink.api.process.BasicFlinkSink

case class LoggingSink(prefix: String) extends BasicFlinkSink with LazyLogging {

  override def testDataOutput: Option[Any => String] =
    Some(value => s"$prefix - $value")

  override def toFlinkFunction: SinkFunction[Any] = new SinkFunction[Any] with Serializable {
    override def invoke(value: Any, context: Context[_]): Unit = {
      val loggedMessage = s"$prefix - $value"
      logger.info(loggedMessage)
    }
  }
}
