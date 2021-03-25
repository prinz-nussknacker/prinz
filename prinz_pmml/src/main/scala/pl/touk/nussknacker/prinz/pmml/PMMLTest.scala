package pl.touk.nussknacker.prinz.pmml

import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import pl.touk.nussknacker.prinz.pmml.repository.{HttpPMMLModelRepository, PMMLModelRepository}

import java.io.{BufferedReader, InputStreamReader}

// TODO to be removed after full repository implementation
object PMMLTest extends App with LazyLogging {
  implicit val config: Config = ConfigFactory.parseString(s"""
      |  pmml {
      |    modelsDirectory: "http://localhost:5100/"
      |    modelDirectoryHrefSelector: "body > ul > li > a"
      |  }
      |""".stripMargin
  )
  implicit val pmmlConfig: PMMLConfig = PMMLConfig()
  val repository = new HttpPMMLModelRepository(pmmlConfig.modelDirectoryHrefSelector)
  val builder = new StringBuilder()
  val res = repository.listModels.right.get

    res.map(_.getName)
    .foreach(name => builder.append(name).append(System.lineSeparator()))
  logger.info(builder.toString)
}
