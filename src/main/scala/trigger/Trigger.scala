package trigger

import parser.Table
import statement.{CreateTableStatement, CreateTriggerStatement, DeleteStatement, DropStatement}

class Trigger(val table: Table) {

  def getDropStatements: List[DropStatement] = null

  def getCreateTriggerStatement: CreateTriggerStatement = null

  def getCreateTableStatement: CreateTableStatement = null

  def getClearStatement: DeleteStatement = null
}
