package trigger

import parser.HibernateTypes.HibernateTypes
import parser.Table
import statement._

class Trigger(val table: Table, val prefix: String, val exclude: Seq[HibernateTypes]) {

  def getDropStatements: List[DropStatement] = {
    List(new DropStatement(prefix + table.tableName, DropTypes.TABLE),
      new DropStatement(prefix + table.tableName + "InsertTrigger", DropTypes.TRIGGER),
      new DropStatement(prefix + table.tableName + "UpdateTrigger", DropTypes.TRIGGER),
      new DropStatement(prefix + table.tableName + "DeleteTrigger", DropTypes.TRIGGER))
  }

  def getCreateTriggerStatements: List[CreateTriggerStatement] = {
    List(TriggerUtil.createCommonInsertTrigger(table, prefix),
      TriggerUtil.createCommonUpdateTrigger(table, prefix, exclude),
      TriggerUtil.createCommonDeleteTrigger(table, prefix)
    )
  }

  def getCreateTableStatement: CreateTableStatement = {
    TriggerUtil.createCommonTable(prefix + table.tableName)
  }

  def getClearStatement: DeleteStatement = new DeleteStatement(prefix + table.tableName)
}
