package de.cooperateproject.incrementalsync.dbutils.statement

import java.text.MessageFormat

import de.cooperateproject.incrementalsync.dbutils.statement.TriggerTypes.TriggerTypes

/**
  * A sql statement to create a MySQL Trigger.
  *
  * @param triggerName the name of the trigger
  * @param triggerType the type of the trigger (INSERT, UPDATE, DELETE)
  * @param tableName   the name of the table to listen to
  * @param forEachRow  true, if code should be executed for each affected row
  * @param code        the code (e.g. statements) to execute when triggered
  */
class CreateTriggerStatement(val triggerName: String,
                             val triggerType: TriggerTypes,
                             val tableName: String,
                             val forEachRow: Boolean,
                             val code: String) extends Statement {

  val CREATE_TRIGGER_FORMAT = new MessageFormat(
    """DELIMITER $
      |CREATE TRIGGER `{0}` AFTER {1} ON `{2}`
      |{3}
      |BEGIN
      |{4}
      |END $
      |DELIMITER ;""".stripMargin)

  /**
    * Creates the textual representation of the sql statement for MySQL Database Systems.
    *
    * @return A String, ready to be executed.
    */
  override def toString: String = {

    CREATE_TRIGGER_FORMAT.format(Array(triggerName, triggerType.toString, tableName,
      if (forEachRow) "FOR EACH ROW" else "", code))

    /* var returnString = "DELIMITER $\nCREATE TRIGGER `" + triggerName + "` AFTER " +
       triggerType.toString + " ON `" + tableName + "`\n"

     if (forEachRow) returnString += "FOR EACH ROW\n"

     returnString += "BEGIN\n" + code + "\nEND $\nDELIMITER ;"

     returnString*/
  }

}

/**
  * Represents the three types of MySQL Trigger: INSERT, UPDATE and DELETE.
  */
object TriggerTypes extends Enumeration {
  type TriggerTypes = Value
  val UPDATE, INSERT, DELETE = Value
}
