package de.cooperateproject.incrementalsync.dbutils.statement

import java.text.MessageFormat

/**
  * A SQL de.cooperateproject.incrementalsync.dbutils.statement to create very simple regular persistent events.
  *
  * @param eventName         the name of the event
  * @param intervalInMinutes the event interval in minutes
  * @param code              the code that should be executed
  */
class CreateSimpleEventStatement(val eventName: String,
                                 val intervalInMinutes: Int,
                                 val code: String) extends Statement {

  private val CREATE_EVENT_FORMAT = new MessageFormat(
    """CREATE EVENT `{0}`
      |ON SCHEDULE EVERY {1} MINUTE
      |ON COMPLETION PRESERVE
      |ENABLE
      |DO
      |{2};""".stripMargin)

  /**
    * Creates the textual representation of the sql de.cooperateproject.incrementalsync.dbutils.statement for MySQL Database Systems.
    *
    * @return A String, ready to be executed.
    */
  override def toString: String = {

    CREATE_EVENT_FORMAT.format(Array(eventName, intervalInMinutes, code))
  }

}
