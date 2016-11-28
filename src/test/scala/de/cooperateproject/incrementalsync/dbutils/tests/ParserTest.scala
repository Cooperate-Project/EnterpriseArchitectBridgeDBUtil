package de.cooperateproject.incrementalsync.dbutils.tests

import java.io.File

import de.cooperateproject.incrementalsync.dbutils.parser.{Parser, Table}
import org.scalatest.FunSuite

class ParserTest extends FunSuite {

  test("Parser properly reads a test input file.") {

    val parser = new Parser(new File("src\\test\\resources\\test.xml"), false)
    val tables = parser.parseXML

    // 2 Classes / Tables
    assert(tables.length == 2)

    // First class is named "tableClass1"
    assert(tables.head.tableName == "tableClass1")

    // First class has 4 properties
    assert(tables.head.properties.length == 4)

    // The ID of the first class is ID1
    assert(tables.head.ids.head == "ID1")

    // The second class has 1 many-to-one called "mto1"
    assert(tables(1).manyToOnes.length == 1 && tables(1).manyToOnes.head == "mto1")

    // The second class has 2 composite ID entries
    assert(tables(1).compositeIds.length == 2)
  }

  test("A table holds its properties properly.") {

    val table = Table("TableName", List("mainID"), List("propA", "PropB"), List("mtoA"), List(), List())

    assert(table.tableName == "TableName")
    assert(table.ids.head == "mainID")
    assert(table.properties.length == 2)
    assert(table.manyToOnes.head == "mtoA")
    assert(table.bags.isEmpty && table.compositeIds.isEmpty)

  }
}
