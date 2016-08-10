package statement

import statement.DropTypes.DropTypes

class DropStatement(val tableName: String, val dropType: DropTypes) extends Statement {

  override def toString: String = "DROP " + dropType.toString + " IF EXISTS `" + tableName + "`;"

}

object DropTypes extends Enumeration {
  type DropTypes = Value
  val TABLE, TRIGGER = Value
}