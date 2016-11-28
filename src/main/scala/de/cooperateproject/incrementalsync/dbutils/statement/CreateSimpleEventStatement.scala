package de.cooperateproject.incrementalsync.dbutils.statement

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

  /**
    * Creates the textual representation of the sql de.cooperateproject.incrementalsync.dbutils.statement for MySQL Database Systems.
    *
    * @return A String, ready to be executed.
    */
  override def toString: String = {

    var event = "CREATE EVENT `" + eventName + "`\n"

    event += "ON SCHEDULE EVERY " + intervalInMinutes + " MINUTE\n"

    event += "ON COMPLETION PRESERVE\nENABLE\n"

    event += "DO\n" + code + ";"

    event
  }

}
