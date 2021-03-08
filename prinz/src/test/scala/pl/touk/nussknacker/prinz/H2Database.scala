package pl.touk.nussknacker.prinz

import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpecLike

import java.io.File
import java.sql.{Connection, DriverManager, ResultSet}
import scala.reflect.io.Directory

trait H2Database extends AnyFlatSpecLike with BeforeAndAfterAll {

  private val PARENT_DIR: String = "./"

  private val DATABASE_NAME: String = "prinz-test-db"

  private val DATABASE_DIR: String = s"$PARENT_DIR/$DATABASE_NAME"

  private val DATABASE_URL: String = s"jdbc:h2:$DATABASE_DIR"

  private var connection: Option[Connection] = None

  def executeQuery(query: String): Option[ResultSet] = {
    for {
      stmt <- connection.map(_.createStatement)
    } yield {
      val result = stmt.executeQuery(query)
      stmt.close()
      result
    }
  }

  override protected def afterAll(): Unit = {
    new Directory(new File(PARENT_DIR)).deleteRecursively()
  }

  override protected def beforeAll(): Unit = {
    connection = Some(DriverManager.getConnection(DATABASE_URL))
  }
}
