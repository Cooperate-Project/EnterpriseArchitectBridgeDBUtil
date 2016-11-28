package de.cooperateproject.incrementalsync.dbutils.trigger

import de.cooperateproject.incrementalsync.dbutils.parser.HibernateTypes.HibernateTypes
import de.cooperateproject.incrementalsync.dbutils.parser.Table
import de.cooperateproject.incrementalsync.dbutils.statement._

/**
  * A de.cooperateproject.incrementalsync.dbutils.trigger listens to a sql table and logs changes into a separate table.
  *
  * @param table   the table to log all changes
  * @param prefix  the prefix of the logging table
  * @param exclude specifies, which HibernateTypes / Attributes of a table should be ignored
  */
class Trigger(val table: Table, val prefix: String, val exclude: Seq[HibernateTypes], val intervalInMinutes: Int) {

  /**
    * Creates create-event statements to remove old entries from a logging table.
    *
    * @return
    */
  def getCleanEventStatement: CreateSimpleEventStatement =
  TriggerUtil.createCommonCleanEvent(table, prefix, intervalInMinutes)


  /**
    * Creates drop statements for the logging table and insert-, update- and delete-de.cooperateproject.incrementalsync.dbutils.trigger.
    *
    * @return A list of drop statements
    */
  def getDropStatements: List[DropStatement] = {
    List(new DropStatement(prefix + table.tableName, DropTypes.TABLE),
      new DropStatement(prefix + table.tableName + "InsertTrigger", DropTypes.TRIGGER),
      new DropStatement(prefix + table.tableName + "UpdateTrigger", DropTypes.TRIGGER),
      new DropStatement(prefix + table.tableName + "DeleteTrigger", DropTypes.TRIGGER),
      new DropStatement(prefix + table.tableName + "CleanEvent", DropTypes.EVENT))
  }

  /**
    * Creates createTrigger-statements for insert-, update- and delete-de.cooperateproject.incrementalsync.dbutils.trigger.
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
    * Creates a createTable-de.cooperateproject.incrementalsync.dbutils.statement for the logging table.
    *
    * @return A create de.cooperateproject.incrementalsync.dbutils.statement
    */
  def getCreateTableStatement: CreateTableStatement = {
    TriggerUtil.createCommonTable(prefix + table.tableName)
  }

  /**
    * Creates a delete de.cooperateproject.incrementalsync.dbutils.statement for a logging table.
    *
    * @return A delete de.cooperateproject.incrementalsync.dbutils.statement
    */
  def getClearStatement: DeleteStatement = new DeleteStatement(prefix + table.tableName)

}
