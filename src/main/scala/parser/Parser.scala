package parser

import java.io.File

import parser.HibernateTypes.HibernateTypes

import scala.collection.mutable.ListBuffer
import scala.xml.{NodeSeq, XML}

class Parser(inputFile: File, verbose: Boolean) {

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

  private def getColumns(xmlClass: NodeSeq, hibernateType: HibernateTypes): List[String] = {
    val columns: ListBuffer[String] = ListBuffer[String]()

    val typeString = hibernateType match {
      case HibernateTypes.id => "id"
      case HibernateTypes.property => "property"
      case HibernateTypes.manytoone => "many-to-one"
      case HibernateTypes.bag => "bag"
      case HibernateTypes.`compositeid` => "composite-id"
    }

    // FIXME: Funktioniert nicht für compositeID
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