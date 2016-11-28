package de.cooperateproject.incrementalsync.dbutils.statement

import java.text.MessageFormat

/**
  * A SQL Statement to create a table.
  *
  * @param tableName  The name of the table
  * @param content    A Map with column names (key) and column type (value)
  * @param primaryKey the primary key (content must contain this key / column name)
  */
class CreateTableStatement(val tableName: String,
                           val content: Map[String, String],
                           val primaryKey: String) extends Statement {

  private val CREATE_TABLE_FORMAT = new MessageFormat(
    """CREATE TABLE `{0}`
      |(
      |{1}
      |{2}
      |);""".stripMargin)

  private val TABLE_ROW_FORMAT = new MessageFormat("`{0}` {1},")

  /**
    * A SQL Statement to create a table.
    *
    * @param tableName The name of the table
    * @param content   A Map with column names (key) and column type (value)
    */
  def this(tableName: String, content: Map[String, String]) = this(tableName, content, "")

  /**
    * Creates the textual representation of the sql de.cooperateproject.incrementalsync.dbutils.statement for MySQL Database Systems.
    *
    * @return A String, ready to be executed.
    */
  override def toString: String = {

    val rows = for ((columnName, columnType) <- content)
      yield TABLE_ROW_FORMAT.format(Array(columnName, columnType))

    val key: String =
      if (primaryKey != null && primaryKey.length > 0 && content.contains(primaryKey))
        "PRIMARY KEY (`%s`)".format(primaryKey) else ""

    CREATE_TABLE_FORMAT.format(Array(tableName, rows.mkString("\n"), key))

  }


}
