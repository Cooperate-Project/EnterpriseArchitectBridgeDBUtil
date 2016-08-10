package statement

import parser.Table

object SQLUtil {

  def createCommonTable(tableName: String): CreateTableStatement = {
    // Potenzielle Fehlerquelle: Primary Key (ID) wird immer als INT angenommen
    new CreateTableStatement(tableName,
      Map("ID" -> "INT",
        "Timestamp" -> "TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"),
      "ID")
  }

  def createCommonUpdateTrigger(table: Table): CreateTriggerStatement = {

    // Potenzielle Fehlerquelle: Kann der Primary Key auch anders gespeichert werden?
    val primaryKey = if (table.ids.nonEmpty) table.ids.head
    else if (table.manyToOnes.nonEmpty) table.manyToOnes.head
    else throw new Exception("Primary Key nicht gefunden! (Tabelle: " + table.tableName + ")")

    var code = "IF "
    val iterator = (table.manyToOnes ::: table.properties ::: table.ids ::: table.bags).iterator

    while (iterator.hasNext) {
      val elem = iterator.next()

      if (iterator.hasNext)
        code += "NEW." + elem + " <> OLD." + elem + "OR\n\t"
      else
        code += "NEW." + elem + " <> OLD." + elem + "THEN\n\t"

    }

    code += "REPLACE INTO " + table.tableName + "VALUES(OLD." + primaryKey + ", NOW());\nEND IF;"

    new CreateTriggerStatement(table.tableName, TriggerTypes.UPDATE, table.tableName + "Trigger", true, code)
  }

  // TODO: Insert / Delete Trigger

}
