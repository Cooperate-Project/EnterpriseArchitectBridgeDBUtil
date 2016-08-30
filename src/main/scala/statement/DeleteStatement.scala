package statement

/**
  * A sql statement to delete entries from a table.
  *
  * @param tableName   the name of the table
  * @param whereClause the specification, which entries should be deleted (if empty, everything will be deleted)
  */
class DeleteStatement(val tableName: String, val whereClause: String) extends Statement {

  /**
    * A sql statement to delete entries from a table.
    *
    * @param tableName the name of the table
    */
  def this(tableName: String) = this(tableName, "")

  /**
    * Creates the textual representation of the sql statement for MySQL Database Systems.
    *
    * @return A String, ready to be executed.
    */
  override def toString: String = {
    var returnString = "DELETE FROM `" + tableName + "`"

    if (whereClause != null & whereClause.length > 0)
      returnString += "\nWHERE " + whereClause


    returnString + ";"
  }

}
