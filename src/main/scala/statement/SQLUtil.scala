package statement

import java.io.{File, PrintWriter}
import java.util.Calendar

/**
  * Provides Utility methods to work with sql files and statements.
  */
object SQLUtil {

  private val HEADLINE =
    """/*------------------------------------------------------------*/
      |/* Generated by HibernateTrigger (Sebastian Hahner)           */
      |/* Created on: <date>                  */
      |/* DBMS      : MySql                                          */
      |/* Notes     : Creates Tables and Triggers from Hibernate XML */
      |/*------------------------------------------------------------*/
    """.stripMargin.replace("<date>", Calendar.getInstance.getTime.toString)

  /**
    * Takes a list of generated statements and writes a sql file.
    *
    * @param sqlFile    the destination sql file
    * @param statements a list of statements
    */
  def writeSQL(sqlFile: File, statements: List[Statement]) = {

    var outputString = HEADLINE + "\n\n"

    for (statement <- statements) {
      outputString += statement.toString + "\n\n"
    }

    try {
      new PrintWriter(sqlFile) {
        write(outputString)
        close()
      }
      println("Saved sql file to " + sqlFile.getAbsolutePath)
    } catch {
      case e: Exception => println("ERROR while saving sql file!")
    }

  }

}
