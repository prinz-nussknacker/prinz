package pl.touk.nussknacker.prinz.pmml

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.pmml.repository.PMMLModelRepository

import java.io.{BufferedReader, InputStreamReader}

// TODO to be removed after full repository implementation
object PMMLTest extends App with LazyLogging {
  val d1  = "file:///home/avan1235/Desktop/pmml/fraud-detection.pmml"
  val d2  = "file:///home/avan1235/Desktop/pmml/"
  val d3  = "http://localhost:5100"
  val d4  = "http://localhost:5100/fraud-detection.pmml"
  implicit val config: Config = ConfigFactory.parseString(s"""
      |  pmml {
      |    modelsDirectory: "$d4"
      |    modelDirectoryHrefSelector: "body > ul > li > a"
      |  }
      |""".stripMargin
  )
  implicit val pmmlConfig: PMMLConfig = PMMLConfig()
  val repository = new PMMLModelRepository
  val builder = new StringBuilder()
  repository.listModels.right.get
    .map(_.inputStream)
    .map(new InputStreamReader(_))
    .map(new BufferedReader(_))
    .foreach(_.lines().forEach(line => builder.append(line)))
  logger.info(builder.toString)
}
