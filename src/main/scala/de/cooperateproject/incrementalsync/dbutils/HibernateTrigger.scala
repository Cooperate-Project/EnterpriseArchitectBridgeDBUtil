package de.cooperateproject.incrementalsync.dbutils

import java.io.File

import de.cooperateproject.incrementalsync.dbutils.parser.HibernateTypes.HibernateTypes
import de.cooperateproject.incrementalsync.dbutils.parser.{HibernateTypes, Parser}
import de.cooperateproject.incrementalsync.dbutils.statement.SQLUtil.writeWithPrintWriter
import de.cooperateproject.incrementalsync.dbutils.statement.{SQLUtil, Statement}
import de.cooperateproject.incrementalsync.dbutils.trigger.Trigger

import scala.collection.mutable.ListBuffer

/**
  * Created by seb on 28.11.2016.
  */
object HibernateTrigger {

  // Used to parse HibernateTypes in the command line input
  implicit val excludeRead: scopt.Read[HibernateTypes.Value] =
    scopt.Read.reads(HibernateTypes withName _.toLowerCase)

  // This is the command line parser (scopt library)
  val argsParser = new scopt.OptionParser[Config]("HibernateTrigger") {
    head("Hibernate XML to SQL Trigger Generator")

    arg[File]("<input xml>").required().action((x, c) =>
      c.copy(inputFile = x)).
      validate(x => if (x.exists()) success else failure("Input file not found")).
      text("Specify input hibernate xml file")

    arg[File]("<output sql>").action((x, c) =>
      c.copy(outputFile = x)).text("Specify output sql file")

    opt[String]('p', "prefix").valueName("<prefix>").action((x, c) =>
      c.copy(prefix = x)).validate(x => if (x.length < 1) failure("Empty prefix") else success).
      text("Table and trigger name prefix in database")

    opt[Unit]('r', "reset").action((_, c) =>
      c.copy(clear = true)).text("Creates statements to empty all trigger tables (with a proper mapping provided).")

    opt[Unit]('v', "verbose").action((_, c) =>
      c.copy(verbose = true)).text("Enables detailed console output")

    opt[File]('d', "debug").valueName("<file>").action((x, c) =>
      c.copy(debug = x)).text("Prints all parsed Tables and Columns into an debug file")

    opt[Int]('e', "event").valueName("<minutes>").action((x, c) =>
      c.copy(eventInterval = x)).text("Event interval when old logging entries get removed (Minutes). Set to 0 or -1 to disable removal.")

    opt[Seq[HibernateTypes]]('e', "exclude").valueName("<type>,<type>,...").action((x, c) =>
      c.copy(exclude = x)).text("Does exclude sql commands for specific hibernate-types (ID, Property, ManyToOne, Bag, CompositeID). Default: Bag")

    opt[Unit]('c', "clear").action((_, c) =>
      c.copy(reset = true)).text("Creates statements to drop all tables and triggers of the provided mapping.")

    help("help").hidden().text("prints help")

  }

  def main(args: Array[String]): Unit = {

    argsParser.parse(args, Config()) match {
      case None => // argument fail
      case Some(config) =>

        // Create Parser and parse hibernate xml
        println("Step 1 of 3: Parsing Hibernate XML File")
        val parser = new Parser(config.inputFile, config.verbose)
        val tables = parser.parseXML

        if (config.debug != null)
          saveDebugOutput(tables, config)

        // Create Trigger Objects & Statements
        println("Step 2 of 3: Creating Triggers")
        val triggers = for (table <- tables) yield new Trigger(table, config.prefix, config.exclude, config.eventInterval)
        var statements = ListBuffer[Statement]()

        // Create Delete Statements if specified
        if (config.clear) {
          if (config.verbose) println("Generating Clear Statements")
          for (trigger <- triggers)
            statements += trigger.getClearStatement

        } else {
          // Create Drop Statements
          if (config.verbose) println("Generating Drop Statements")
          for (trigger <- triggers) {
            statements ++= trigger.getDropStatements
          }
          // Create Create Statements
          if (!config.reset) {
            if (config.verbose) println("Generating Create Statements")
            for (trigger <- triggers) {
              statements += trigger.getCreateTableStatement
              statements ++= trigger.getCreateTriggerStatements

              if (config.eventInterval > 0) {
                statements += trigger.getCleanEventStatement
              }
            }
          }
        }

        // Print Statements
        println("Step 3 of 3: Writing SQL File")
        SQLUtil.writeSQL(config.outputFile, statements.toList)

    }
  }

  /**
    * Saves the debug output. This is information about parsed tables and columns.
    *
    * @param tables the tables to print information
    * @param config the config with all command line arguments / options
    */
  private def saveDebugOutput(tables: List[parser.Table], config: Config): Unit = {

    println("Generating Debug Output...")

    var outputStringBuilder: StringBuilder = new StringBuilder("Info: The Debug Output ignores the exclude-flag!\n")

    for (table <- tables) {

      outputStringBuilder append "\n" + table.tableName

      outputStringBuilder append "\n\tbags:\t\t"
      for (bag <- table.bags) outputStringBuilder append bag + ", "

      outputStringBuilder append "\n\tids:\t\t"
      for (id <- table.ids) outputStringBuilder append id + ", "

      outputStringBuilder append "\n\tproperties:\t"
      for (prop <- table.properties) outputStringBuilder append prop + ", "

      outputStringBuilder append "\n\tonetomanys:\t"
      for (otm <- table.manyToOnes) outputStringBuilder append otm + ", "

      outputStringBuilder append "\n\tcompositeIds: "
      for (cid <- table.compositeIds) outputStringBuilder append cid + ", "

    }

    println("Generated Debug File. Saving now...")

    writeWithPrintWriter(config.debug, outputStringBuilder.toString, "Saved debug file to ", "ERROR while saving debug file!")

  }

  /**
    * This config specifies all command line options
    *
    * @param prefix        the prefix of the logging table
    * @param clear         true, if delete statements should be created
    * @param verbose       true, if there should be a verbose console output
    * @param debug         a file for debug output or nothing
    * @param exclude       HibernateTypes to exclude from updateTrigger-listening
    * @param reset         true, if only drop Statements should be created
    * @param inputFile     the input hibernate xml file
    * @param outputFile    the output sql file
    * @param eventInterval the interval when old logging entries will get removed
    */
  case class Config(prefix: String = "ht_",
                    clear: Boolean = false,
                    verbose: Boolean = false,
                    debug: File = null,
                    exclude: Seq[HibernateTypes] = Seq(HibernateTypes.bag),
                    reset: Boolean = false,
                    inputFile: File = null,
                    outputFile: File = null,
                    eventInterval: Int = 1
                   )

}
