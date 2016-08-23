package trigger

import parser.Table
import statement.{CreateTableStatement, CreateTriggerStatement, TriggerTypes}

object TriggerUtil {

  // TODO: Offen: Trigger verifizieren, Doku, Tests, exclude
  // TODO: Offen: Performance-Test, Konstruierter Listen_Fall (Elements, size, richtig geladen?) (eclipse),

  private[trigger] def createCommonTable(tableName: String): CreateTableStatement = {
    // FIXME: Potenzielle Fehlerquelle: Primary Key (ID) wird immer als INT angenommen
    new CreateTableStatement(tableName,
      Map("ID" -> "INT",
        "Timestamp" -> "TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"),
      "ID")
  }

  private[trigger] def createCommonUpdateTrigger(table: Table, prefix: String): CreateTriggerStatement = {

    val primaryKey = getPrimaryKey(table)

    var code = "IF "
    val iterator = (table.manyToOnes ::: table.properties ::: table.ids ::: table.bags ::: table.compositeIds).iterator

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

  private[trigger] def createCommonInsertTrigger(table: Table, prefix: String): CreateTriggerStatement =
    createInsertOrDeleteTrigger(table, prefix, isInsertTrigger = true)

  private[this] def createInsertOrDeleteTrigger(table: Table, prefix: String, isInsertTrigger: Boolean): CreateTriggerStatement = {

    val primaryKey = getPrimaryKey(table)
    val varName = if (isInsertTrigger) "NEW" else "OLD" // TODO: Verifizieren (NEW oder Inserted?, evtl. auch before delete?)
    val triggerPrefix = if (isInsertTrigger) "Insert" else "Delete"
    val triggerType = if (isInsertTrigger) TriggerTypes.INSERT else TriggerTypes.DELETE

    val code = "REPLACE INTO " + prefix + table.tableName + " VALUES(" + varName + "." + primaryKey + ", NOW());"

    new CreateTriggerStatement(prefix + table.tableName + triggerPrefix + "Trigger", triggerType, table.tableName, true, code)
  }

  private[this] def getPrimaryKey(table: Table): String = {
    // FIXME: Potenzielle Fehlerquelle: Kann der Primary Key auch anders gespeichert werden?
    // FIXME: Composite ID (nur der erste Primary Key wird verwendet. Variables Tabellenlayout notwendig?)
    if (table.ids.nonEmpty) table.ids.head
    else if (table.compositeIds.nonEmpty) table.compositeIds.head
    else throw new Exception("Primary Key nicht gefunden! (Tabelle: " + table.tableName + ")")
  }

  private[trigger] def createCommonDeleteTrigger(table: Table, prefix: String): CreateTriggerStatement =
    createInsertOrDeleteTrigger(table, prefix, isInsertTrigger = false)

}
