package statement

class CreateTableStatement(val tableName: String,
                           val content: Map[String, String],
                           val primaryKey: String) extends Statement {

  def this(tableName: String, content: Map[String, String]) = this(tableName, content, "")

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
