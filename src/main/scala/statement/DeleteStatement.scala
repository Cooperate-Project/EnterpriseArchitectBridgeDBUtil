package statement

class DeleteStatement(val tableName: String, val whereClause: String) {

  def this(tableName: String) = this(tableName, "")

  override def toString: String = {
    var returnString = "DELETE FROM `" + tableName + "`"

    if (whereClause != null & whereClause.length > 0)
      returnString += "\nWHERE " + whereClause


    returnString + ";"
  }

}
