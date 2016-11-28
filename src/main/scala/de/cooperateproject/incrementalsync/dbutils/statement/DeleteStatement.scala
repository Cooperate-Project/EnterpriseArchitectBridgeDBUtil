package de.cooperateproject.incrementalsync.dbutils.statement

import java.text.MessageFormat

/**
  * A sql de.cooperateproject.incrementalsync.dbutils.statement to delete entries from a table.
  *
  * @param tableName   the name of the table
  * @param whereClause the specification, which entries should be deleted (if empty, everything will be deleted)
  */
class DeleteStatement(val tableName: String, val whereClause: String) extends Statement {

  private val DELETE_FORMAT = new MessageFormat(
    """DELETE FROM `{0}`{1};""".stripMargin)

  /**
    * A sql statement to delete entries from a table.
    *
    * @param tableName the name of the table
    */
  def this(tableName: String) = this(tableName, "")

  /**
    * Creates the textual representation of the statement for MySQL Database Systems.
    *
    * @return A String, ready to be executed.
    */
  override def toString: String = {

    DELETE_FORMAT.format(Array(tableName,
      if (whereClause != null & whereClause.length > 0) "\nWHERE " + whereClause else ""))

  }

}
