package statement

import statement.DropTypes.DropTypes

/**
  * A sql statement to drop a table or a trigger.
  *
  * @param name     the name of the table or trigger
  * @param dropType the typ of the drop statement (TABLE or TRIGGER)
  */
class DropStatement(val name: String, val dropType: DropTypes) extends Statement {

  /**
    * Creates the textual representation of the sql statement for MySQL Database Systems.
    *
    * @return A String, ready to be executed.
    */
  override def toString: String = "DROP " + dropType.toString + " IF EXISTS `" + name + "`;"

}

/**
  * Represents the types of a drop statement.
  */
object DropTypes extends Enumeration {
  type DropTypes = Value
  val TABLE, TRIGGER, EVENT = Value
}