package de.cooperateproject.incrementalsync.dbutils.statement

import de.cooperateproject.incrementalsync.dbutils.statement.TriggerTypes.TriggerTypes

/**
  * A sql de.cooperateproject.incrementalsync.dbutils.statement to create a MySQL Trigger.
  *
  * @param triggerName the name of the de.cooperateproject.incrementalsync.dbutils.trigger
  * @param triggerType the type of the de.cooperateproject.incrementalsync.dbutils.trigger (INSERT, UPDATE, DELETE)
  * @param tableName   the name of the table to listen to
  * @param forEachRow  true, if code should be executed for each affected row
  * @param code        the code (e.g. statements) to execute when triggered
  */
class CreateTriggerStatement(val triggerName: String,
                             val triggerType: TriggerTypes,
                             val tableName: String,
                             val forEachRow: Boolean,
                             val code: String) extends Statement {

  /**
    * Creates the textual representation of the sql de.cooperateproject.incrementalsync.dbutils.statement for MySQL Database Systems.
    *
    * @return A String, ready to be executed.
    */
  override def toString: String = {
    var returnString = "DELIMITER $\nCREATE TRIGGER `" + triggerName + "` AFTER " +
      triggerType.toString + " ON `" + tableName + "`\n"

    if (forEachRow) returnString += "FOR EACH ROW\n"

    returnString += "BEGIN\n" + code + "\nEND $\nDELIMITER ;"

    returnString
  }

}

/**
  * Represents the three types of MySQL Trigger: INSERT, UPDATE and DELETE.
  */
object TriggerTypes extends Enumeration {
  type TriggerTypes = Value
  val UPDATE, INSERT, DELETE = Value
}
