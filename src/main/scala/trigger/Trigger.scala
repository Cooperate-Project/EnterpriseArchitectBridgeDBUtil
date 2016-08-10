package trigger

import parser.Table
import statement._

class Trigger(val table: Table) {

  def getDropStatements: List[DropStatement] = null

  def getCreateTriggerStatements: List[CreateTriggerStatement] = null

  def getCreateTableStatement: CreateTableStatement = null

  def getClearStatement: DeleteStatement = null
}
