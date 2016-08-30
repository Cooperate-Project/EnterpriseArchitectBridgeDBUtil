package parser

import java.io.File

import parser.HibernateTypes.HibernateTypes

import scala.collection.mutable.ListBuffer
import scala.xml.{NodeSeq, XML}

/**
  * A Parser can read xml files and return all tables and attributes.
  * This parser is used for the generated hibernate.hbm.xml.
  *
  * @param inputFile a hibernate xml file to get information from
  * @param verbose   true, if there should be additional logging output
  */
class Parser(inputFile: File, verbose: Boolean) {

  /**
    * Opens and parses the input xml file. The format fits to hibernate xml files.
    *
    * @return A list of tables with information about name and columns.
    */
  def parseXML: List[Table] = {

    val returnTables: ListBuffer[Table] = ListBuffer[Table]()

    // XML Parsing
    val hibernateXML: NodeSeq = XML.loadFile(inputFile)

    if (verbose) println("Read input file: " + inputFile.getAbsolutePath)

    for (xmlClass <- hibernateXML \\ "class") {

      val tableName = (xmlClass \ "@table").text.replace("`", "")
      if (verbose) println("Parsing class: " + tableName)

      val ids = getColumns(xmlClass, HibernateTypes.id)
      val properties = getColumns(xmlClass, HibernateTypes.property)
      val manyToOnes = getColumns(xmlClass, HibernateTypes.manytoone)
      val bags = getColumns(xmlClass, HibernateTypes.bag)
      val compositeIds = getColumns(xmlClass, HibernateTypes.compositeid)

      returnTables += Table(tableName, ids, properties, manyToOnes, bags, compositeIds)
    }

    returnTables.toList
  }

  /**
    * Helper method to get all Columns of a table.
    *
    * @param xmlClass      The table to read (already parsed xml)
    * @param hibernateType The type of columns to parse
    * @return A list of column names of the specified type
    */
  private def getColumns(xmlClass: NodeSeq, hibernateType: HibernateTypes): List[String] = {
    val columns: ListBuffer[String] = ListBuffer[String]()

    val typeString = hibernateType match {
      case HibernateTypes.id => "id"
      case HibernateTypes.property => "property"
      case HibernateTypes.manytoone => "many-to-one"
      case HibernateTypes.bag => "bag"
      case HibernateTypes.`compositeid` => "composite-id"
    }

    for (xmlElements <- xmlClass \\ typeString) {

      for (column <- xmlElements \\ "column") {
        val columnClear = (column \ "@name").text.replace("`", "")
        if (verbose) println("Found column: " + columnClear + " [" + hibernateType.toString + "]")
        columns += columnClear
      }


    }

    columns.toList
  }

}