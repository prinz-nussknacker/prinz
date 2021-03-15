package pl.touk.nussknacker.prinz

import java.io.File
import java.sql.{Connection, DriverManager, ResultSet}
import scala.reflect.io.Directory

object H2Database {

  private val DATABASE_PARENT_DIR: String = "./h2-test-database"

  private val DATABASE_NAME: String = s"prinz-test-db"

  private val DATABASE_DIR: String = s"$DATABASE_PARENT_DIR/$DATABASE_NAME"

  private val DATABASE_URL: String = s"jdbc:h2:$DATABASE_DIR"

  private val connection: Connection = {
    cleanDatabase()
    DriverManager.getConnection(DATABASE_URL)
  }

  def executeNonEmptyQuery(query: String): ResultSet = {
    val stmt = connection.createStatement
    val rs = stmt.executeQuery(query)
    if (rs.next()) {
      rs
    }
    else {
      throw new IllegalArgumentException("Empty ResultSet")
    }
  }

  def executeUpdate(query: String): Int = {
    val stmt = connection.createStatement
    stmt.executeUpdate(query)
  }

  private def cleanDatabase(): Unit = {
    new Directory(new File(DATABASE_PARENT_DIR)).deleteRecursively()
  }
}
