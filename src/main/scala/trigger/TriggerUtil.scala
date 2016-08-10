package trigger

import parser.Table
import statement.{CreateTableStatement, CreateTriggerStatement, TriggerTypes}

object TriggerUtil {

  private[trigger] def createCommonTable(tableName: String): CreateTableStatement = {
    // Potenzielle Fehlerquelle: Primary Key (ID) wird immer als INT angenommen
    new CreateTableStatement(tableName,
      Map("ID" -> "INT",
        "Timestamp" -> "TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"),
      "ID")
  }

  private[trigger] def createCommonUpdateTrigger(table: Table, prefix: String): CreateTriggerStatement = {

    // Potenzielle Fehlerquelle: Kann der Primary Key auch anders gespeichert werden?
    val primaryKey = if (table.ids.nonEmpty) table.ids.head
    else if (table.manyToOnes.nonEmpty) table.manyToOnes.head
    else throw new Exception("Primary Key nicht gefunden! (Tabelle: " + table.tableName + ")")

    var code = "IF "
    val iterator = (table.manyToOnes ::: table.properties ::: table.ids ::: table.bags).iterator

    while (iterator.hasNext) {
      val elem = iterator.next()

      if (iterator.hasNext)
        code += "NEW." + elem + " <> OLD." + elem + " OR\n\t"
      else
        code += "NEW." + elem + " <> OLD." + elem + " THEN\n\t"

    }

    code += "REPLACE INTO " + prefix + table.tableName + " VALUES(OLD." + primaryKey + ", NOW());\nEND IF;"

    new CreateTriggerStatement(prefix + table.tableName + "Trigger", TriggerTypes.UPDATE, table.tableName, true, code)
  }

  // TODO: Insert / Delete Trigger


}
