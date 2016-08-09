import java.io.File

import parser.HibernateTypes.HibernateTypes
import parser.{HibernateTypes, Parser}

object HibernateTrigger {

  implicit val excludeRead: scopt.Read[HibernateTypes.Value] =
    scopt.Read.reads(HibernateTypes withName _.toLowerCase)
  val argsParser = new scopt.OptionParser[Config]("HibernateTrigger") {
    head("Hibernate XML to SQL Trigger Generator")

    arg[File]("<input xml>").required().action((x, c) =>
      c.copy(inputFile = x)).
      validate(x => if (x.exists()) success else failure("Input file not found")).
      text("Specify input hibernate xml file")

    arg[File]("<output sql>").optional().action((x, c) =>
      c.copy(outputFile = x)).text("Specify output sql file")

    opt[String]('p', "prefix").valueName("<prefix>").action((x, c) =>
      c.copy(prefix = x)).text("Table and trigger name prefix in database")

    opt[Unit]('c', "clear").action((_, c) =>
      c.copy(clear = true)).text("Creates statements to empty all trigger tables")

    opt[Unit]('v', "verbose").action((_, c) =>
      c.copy(verbose = true)).text("Enables detailed console output")

    opt[File]('d', "debug").valueName("<file>").action((x, c) =>
      c.copy(debug = x)).text("Prints all parsed Tables and Columns into an debug file")

    opt[Seq[HibernateTypes]]('e', "exclude").valueName("<type>,<type>,...").action((x, c) =>
      c.copy(exclude = x)).text("Does not print specific hibernate-types (ID, Property, ManyToOne, Bag)")

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

        // Create Trigger Object
        println("Step 2 of 3: Creating Triggers")

        // TODO

        // Create Statements
        println("Step 3 of 3: Writing SQL File")

      // TODO

    }

  }

  case class Config(prefix: String = "",
                    clear: Boolean = false,
                    verbose: Boolean = false,
                    debug: File = null,
                    exclude: Seq[HibernateTypes] = Seq(),
                    reset: Boolean = false,
                    inputFile: File = null,
                    outputFile: File = null
                   )

}
