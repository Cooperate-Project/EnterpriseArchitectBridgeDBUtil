package trigger

import parser.Table
import statement._

class Trigger(val table: Table, val prefix: String) {

  def getDropStatements: List[DropStatement] = {
    List(new DropStatement(prefix + table.tableName, DropTypes.TABLE),
      new DropStatement(prefix + table.tableName + "Trigger", DropTypes.TRIGGER))
  }

  def getCreateTriggerStatements: List[CreateTriggerStatement] = {
    List(TriggerUtil.createCommonUpdateTrigger(table, prefix))
    // TODO: Insert / Delete
  }

  def getCreateTableStatement: CreateTableStatement = {
    TriggerUtil.createCommonTable(prefix + table.tableName)
  }

  def getClearStatement: DeleteStatement = new DeleteStatement(prefix + table.tableName)
}
