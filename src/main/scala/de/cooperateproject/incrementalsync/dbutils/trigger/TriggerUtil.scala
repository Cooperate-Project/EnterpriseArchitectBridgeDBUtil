package de.cooperateproject.incrementalsync.dbutils.trigger

import de.cooperateproject.incrementalsync.dbutils.parser.HibernateTypes.HibernateTypes
import de.cooperateproject.incrementalsync.dbutils.parser.{HibernateTypes, Table}
import de.cooperateproject.incrementalsync.dbutils.statement._

/**
  * Provides Utility Function to create common SQL statements for trigger & other.
  */
object TriggerUtil {

  /**
    * Creates a common createTable-Statements with two columns (entity-ID and modified date).
    *
    * @param tableName the name of the logging table to create
    * @return a CreateTabble-Statement
    */
  private[trigger] def createCommonTable(tableName: String): CreateTableStatement = {
    // FIXME: Primary Key always INT?
    new CreateTableStatement(tableName,
      Map("ID" -> "INT",
        "Timestamp" -> "TIMESTAMP(6) NULL DEFAULT NULL"),
      "ID")
  }

  /**
    * Creates a common createTrigger-Statement for update triggers.
    *
    * @param table   the table to listen to
    * @param prefix  the prefix of the logging tables
    * @param exclude all HibernateTypes that should be excluded from the listen proccess
    * @return a CreateTrigger-Statement
    */
  private[trigger] def createCommonUpdateTrigger(table: Table, prefix: String, exclude: Seq[HibernateTypes]): CreateTriggerStatement = {

    val primaryKey = getPrimaryKey(table)

    var code = "IF "
    val iterator =
      ((if (exclude.contains(HibernateTypes.manytoone)) Nil else table.manyToOnes) :::
        (if (exclude.contains(HibernateTypes.property)) Nil else table.properties) :::
        (if (exclude.contains(HibernateTypes.id)) Nil else table.ids) :::
        (if (exclude.contains(HibernateTypes.bag)) Nil else table.bags) :::
        (if (exclude.contains(HibernateTypes.compositeid)) Nil else table.compositeIds)).iterator

    while (iterator.hasNext) {
      val elem = iterator.next()

      if (iterator.hasNext)
        code += "NEW." + elem + " <> OLD." + elem + " OR\n\t"
      else
        code += "NEW." + elem + " <> OLD." + elem + " THEN\n\t"

    }

    code += "REPLACE INTO " + prefix + table.tableName + " VALUES(OLD." + primaryKey + ", NOW(6));\nEND IF;"

    new CreateTriggerStatement(prefix + table.tableName + "UpdateTrigger", TriggerTypes.UPDATE, table.tableName, true, code)
  }

  /**
    * Creates a createTrigger-Statement for empty logging table triggers.
    *
    * @param table            the table for which the logging table was created for
    * @param prefix           the prefix of the logging table
    * @param timeoutInSeconds the timeout after which old values will be removed
    * @return
    */
  private[trigger] def createEmptyTriggerTableTrigger(table: Table, prefix: String, timeoutInSeconds: Int): CreateTriggerStatement = {

    val code = new DeleteStatement(prefix + table.tableName,
      "`Timestamp` < DATE_SUB(NOW(6), INTERVAL " + timeoutInSeconds + " SECOND)").toString

    new CreateTriggerStatement(prefix + table.tableName + "EmptyTrigger", TriggerTypes.INSERT, prefix + table.tableName, true, code)
  }

  /**
    * Creates a createTrigger-Statement for insert triggers.
    *
    * @param table  the table to listen to
    * @param prefix the prefix of the logging table
    * @return a CreateTrigger-Statement
    */
  private[trigger] def createCommonInsertTrigger(table: Table, prefix: String): CreateTriggerStatement =
  createInsertOrDeleteTrigger(table, prefix, isInsertTrigger = true)

  /**
    * This private function is used to create Insert or Delete triggers (nearly everything is common)
    *
    * @param table           the table to listen to
    * @param prefix          the prefix of the logging table
    * @param isInsertTrigger true, if a statement for Insert Triggers should be created
    *                        false, if it should be a Delete Trigger
    * @return a CreateTrigger-Statement
    */
  private[this] def createInsertOrDeleteTrigger(table: Table, prefix: String, isInsertTrigger: Boolean): CreateTriggerStatement = {

    val primaryKey = getPrimaryKey(table)
    val varName = if (isInsertTrigger) "NEW" else "OLD"
    val triggerPrefix = if (isInsertTrigger) "Insert" else "Delete"
    val triggerType = if (isInsertTrigger) TriggerTypes.INSERT else TriggerTypes.DELETE

    val code = "REPLACE INTO " + prefix + table.tableName + " VALUES(" + varName + "." + primaryKey + ", NOW(6));"

    new CreateTriggerStatement(prefix + table.tableName + triggerPrefix + "Trigger", triggerType, table.tableName, true, code)
  }

  /**
    * This private functions tries to get the primary key from a table (this might fail!)
    *
    * @param table the table to look up a primary key
    * @return the attribute with the highest percentage to be the primary key
    */
  private[this] def getPrimaryKey(table: Table): String = {
    // FIXME: Composite ID is currently not supported
    if (table.ids.nonEmpty) table.ids.head
    else if (table.compositeIds.nonEmpty) table.compositeIds.head
    else throw new Exception("Primary Key nicht gefunden! (Tabelle: " + table.tableName + ")")
  }

  /**
    * Creates a createTrigger-Statement for delete triggers.
    *
    * @param table  the table to listen to
    * @param prefix the prefix of the logging table
    * @return a CreateTrigger-Statement
    */
  private[trigger] def createCommonDeleteTrigger(table: Table, prefix: String): CreateTriggerStatement =
    createInsertOrDeleteTrigger(table, prefix, isInsertTrigger = false)

  private[trigger] def createCommonCleanEvent(table: Table, prefix: String, intervalInMinutes: Int) = {
    val code = "DELETE FROM `" + prefix + table.tableName +
      "` WHERE `Timestamp` < DATE_SUB(NOW(6), INTERVAL " + intervalInMinutes + " MINUTE)"
    new CreateSimpleEventStatement(prefix + table.tableName + "CleanEvent", intervalInMinutes, code)
  }

}
