package trigger

import parser.HibernateTypes.HibernateTypes
import parser.Table
import statement._

/**
  * A trigger listens to a sql table and logs changes into a separate table.
  *
  * @param table   the table to log all changes
  * @param prefix  the prefix of the logging table
  * @param exclude specifies, which HibernateTypes / Attributes of a table should be ignored
  */
class Trigger(val table: Table, val prefix: String, val exclude: Seq[HibernateTypes]) {

  /**
    * Creates drop statements for the logging table and insert-, update- and delete-trigger.
    *
    * @return A list of drop statements
    */
  def getDropStatements: List[DropStatement] = {
    List(new DropStatement(prefix + table.tableName, DropTypes.TABLE),
      new DropStatement(prefix + table.tableName + "InsertTrigger", DropTypes.TRIGGER),
      new DropStatement(prefix + table.tableName + "UpdateTrigger", DropTypes.TRIGGER),
      new DropStatement(prefix + table.tableName + "DeleteTrigger", DropTypes.TRIGGER))
  }

  /**
    * Creates createTrigger-statements for insert-, update- and delete-trigger.
    *
    * @return A list of create statements
    */
  def getCreateTriggerStatements: List[CreateTriggerStatement] = {
    List(TriggerUtil.createCommonInsertTrigger(table, prefix),
      TriggerUtil.createCommonUpdateTrigger(table, prefix, exclude),
      TriggerUtil.createCommonDeleteTrigger(table, prefix)
    )
  }

  /**
    * Creates a createTable-statement for the loggin table.
    *
    * @return A create statement
    */
  def getCreateTableStatement: CreateTableStatement = {
    TriggerUtil.createCommonTable(prefix + table.tableName)
  }

  /**
    * Creates a delete statement for a logging table.
    *
    * @return A delete statement
    */
  def getClearStatement: DeleteStatement = new DeleteStatement(prefix + table.tableName)
}
