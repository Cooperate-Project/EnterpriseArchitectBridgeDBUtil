import java.io.{File, PrintWriter}

import parser.HibernateTypes.HibernateTypes
import parser.{HibernateTypes, Parser}
import statement.{SQLUtil, Statement}
import trigger.Trigger

import scala.collection.mutable.ListBuffer

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

    opt[Unit]('c', "clear").action((_, c) =>
      c.copy(clear = true)).text("Creates statements to empty all trigger tables")

    opt[Unit]('v', "verbose").action((_, c) =>
      c.copy(verbose = true)).text("Enables detailed console output")

    opt[File]('d', "debug").valueName("<file>").action((x, c) =>
      c.copy(debug = x)).text("Prints all parsed Tables and Columns into an debug file")

    opt[Seq[HibernateTypes]]('e', "exclude").valueName("<type>,<type>,...").action((x, c) =>
      c.copy(exclude = x)).text("Does not print specific hibernate-types (ID, Property, ManyToOne, Bag, CompositeID). Default: Bag")

    opt[Unit]('r', "reset").action((_, c) =>
      c.copy(reset = true)).text("Creates statements to drop all tables and triggers")

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
        val triggers = for (table <- tables) yield new Trigger(table, config.prefix, config.exclude)
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

    var outputString: String = "Info: The Debug Output ignores the exclude-flag!\n"

    for (table <- tables) {

      outputString += "\n" + table.tableName

      outputString += "\n\tbags:\t\t"
      for (bag <- table.bags) outputString += bag + ", "

      outputString += "\n\tids:\t\t"
      for (id <- table.ids) outputString += id + ", "

      outputString += "\n\tproperties:\t"
      for (prop <- table.properties) outputString += prop + ", "

      outputString += "\n\tonetomanys:\t"
      for (otm <- table.manyToOnes) outputString += otm + ", "

      outputString += "\n\tcompositeIds: "
      for (cid <- table.compositeIds) outputString += cid + ", "

    }

    println("Generated Debug File. Saving now...")

    try {
      new PrintWriter(config.debug) {
        write(outputString)
        close()
      }
      println("Saved debug file to " + config.debug.getAbsolutePath)
    } catch {
      case e: Exception => println("ERROR while saving debug file!")
    }

  }

  /**
    * This config specifies all command line options
    *
    * @param prefix     the prefix of the logging table
    * @param clear      true, if delete statements should be created
    * @param verbose    true, if there should be a verbose console output
    * @param debug      a file for debug output or nothing
    * @param exclude    HibernateTypes to exclude from updateTrigger-listening
    * @param reset      true, if only drop Statements should be created
    * @param inputFile  the input hiberante xml file
    * @param outputFile the output sql file
    */
  case class Config(prefix: String = "ht_",
                    clear: Boolean = false,
                    verbose: Boolean = false,
                    debug: File = null,
                    exclude: Seq[HibernateTypes] = Seq(HibernateTypes.bag),
                    reset: Boolean = false,
                    inputFile: File = null,
                    outputFile: File = null
                   )

}
