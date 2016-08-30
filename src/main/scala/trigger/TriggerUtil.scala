package trigger

import parser.HibernateTypes.HibernateTypes
import parser.{HibernateTypes, Table}
import statement.{CreateTableStatement, CreateTriggerStatement, TriggerTypes}

/**
  * Provides Utility Function to create common SQL statements for trigger & other.
  */
object TriggerUtil {

  // TODO: Offen: Refresh-Cascade googlen

  /**
    * Creates a common createTable-Statements with two columns (entity-ID and modified date).
    *
    * @param tableName the name of the logging table to create
    * @return a CreateTabble-Statement
    */
  private[trigger] def createCommonTable(tableName: String): CreateTableStatement = {
    // FIXME: Potenzielle Fehlerquelle: Primary Key (ID) wird immer als INT angenommen
    new CreateTableStatement(tableName,
      Map("ID" -> "INT",
        "Timestamp" -> "TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"),
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

    code += "REPLACE INTO " + prefix + table.tableName + " VALUES(OLD." + primaryKey + ", NOW());\nEND IF;"

    new CreateTriggerStatement(prefix + table.tableName + "UpdateTrigger", TriggerTypes.UPDATE, table.tableName, true, code)
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
    * @param isInsertTrigger true, if a createTrigger-statement for Insert Triggers should be created
    *                        false, if it should be a Delete Trigger
    * @return a CreateTrigger-Statement
    */
  private[this] def createInsertOrDeleteTrigger(table: Table, prefix: String, isInsertTrigger: Boolean): CreateTriggerStatement = {

    val primaryKey = getPrimaryKey(table)
    val varName = if (isInsertTrigger) "NEW" else "OLD"
    val triggerPrefix = if (isInsertTrigger) "Insert" else "Delete"
    val triggerType = if (isInsertTrigger) TriggerTypes.INSERT else TriggerTypes.DELETE

    val code = "REPLACE INTO " + prefix + table.tableName + " VALUES(" + varName + "." + primaryKey + ", NOW());"

    new CreateTriggerStatement(prefix + table.tableName + triggerPrefix + "Trigger", triggerType, table.tableName, true, code)
  }

  /**
    * This private functions tries to get the primary key from a table (this might fail!)
    *
    * @param table the table to look up a primary key
    * @return the attribute with the highest percentage to be the primary key
    */
  private[this] def getPrimaryKey(table: Table): String = {
    // FIXME: Potenzielle Fehlerquelle: Kann der Primary Key auch anders gespeichert werden?
    // FIXME: Composite ID (nur der erste Primary Key wird verwendet. Variables Tabellenlayout notwendig?)
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

}
