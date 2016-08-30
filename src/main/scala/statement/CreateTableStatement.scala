package statement

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

  /**
    * A SQL Statement to create a table.
    *
    * @param tableName The name of the table
    * @param content   A Map with column names (key) and column type (value)
    */
  def this(tableName: String, content: Map[String, String]) = this(tableName, content, "")

  /**
    * Creates the textual representation of the sql statement for MySQL Database Systems.
    *
    * @return A String, ready to be executed.
    */
  override def toString: String = {
    var returnString = "CREATE TABLE `" + tableName + "`\n(\n"

    for ((columnName, columnType) <- content) {
      returnString += "`" + columnName + "` " + columnType + ",\n"
    }

    if (primaryKey != null && primaryKey.length > 0 && content.contains(primaryKey))
      returnString += "PRIMARY KEY (`" + primaryKey + "`)"

    returnString + "\n);"
  }


}
