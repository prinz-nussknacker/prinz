package pl.touk.nussknacker.prinz.sample

import com.typesafe.scalalogging.Logger
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import pl.touk.nussknacker.engine.api.DisplayJson
import pl.touk.nussknacker.engine.flink.api.process.BasicFlinkSink

case object LoggingSink extends BasicFlinkSink {

  private val logger = Logger[this.type]

  override def toFlinkFunction: SinkFunction[Any] = new SinkFunction[Any] {
    override def invoke(value: Any, context: SinkFunction.Context[_]): Unit = {
      logger.info("Data in sink {}", value)
    }
  }

  override def testDataOutput: Option[(Any) => String] = Option {
    case a: DisplayJson => a.asJson.spaces2
    case b: Any => b.toString
  }
}
