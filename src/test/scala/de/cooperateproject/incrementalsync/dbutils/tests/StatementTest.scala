package de.cooperateproject.incrementalsync.dbutils.tests

import de.cooperateproject.incrementalsync.dbutils.statement._
import org.scalatest.FunSuite

class StatementTest extends FunSuite {

  val LINE_BREAK = "[NEWLINE]"

  test("CreateTableStatement prints correctly.") {

    val statement = new CreateTableStatement("tableName", Map("col1" -> "VARCHAR(40)", "col2" -> "INT"), "col2")

    val out =
      """CREATE TABLE `tableName`
        |(
        |`col1` VARCHAR(40),
        |`col2` INT,
        |PRIMARY KEY (`col2`)
        |);""".stripMargin.replaceAll("[\r\n]+", LINE_BREAK)

    assertResult(out) {
      statement.toString.replaceAll("[\r\n]+", LINE_BREAK)
    }

  }

  test("Create Table Statement with wrong or missing key should not set any key.") {

    val statement1 = new CreateTableStatement("tableName", Map("val" -> "int"), "id")
    assert(!statement1.toString.contains("PRIMARY"))

    val statement2 = new CreateTableStatement("tableName", Map("val" -> "int"))
    assert(!statement2.toString.contains("PRIMARY"))
  }

  test("CreateTriggerStatement prints correctly.") {

    val statement = new CreateTriggerStatement("triggerName", TriggerTypes.INSERT, "watchMe", true, "SELECT *;")

    val out =
      """DELIMITER $
        |CREATE TRIGGER `triggerName` AFTER INSERT ON `watchMe`
        |FOR EACH ROW
        |BEGIN
        |SELECT *;
        |END $
        |DELIMITER ;""".stripMargin.replaceAll("[\r\n]+", LINE_BREAK)

    assertResult(out) {
      statement.toString.replaceAll("[\r\n]+", LINE_BREAK)
    }

  }

  test("DeleteStatement prints correctly.") {

    val statement = new DeleteStatement("tableName", "ID = 1")

    val out =
      """DELETE FROM `tableName`
        |WHERE ID = 1;""".stripMargin.replaceAll("[\r\n]+", LINE_BREAK)

    assertResult(out) {
      statement.toString.replaceAll("[\r\n]+", LINE_BREAK)
    }

  }

  test("EventStatement prints correctly.") {

    val statement = new CreateSimpleEventStatement("myEvent", 3, "CODE")

    println(statement.toString)

    val out =
      """CREATE EVENT `myEvent`
        |ON SCHEDULE EVERY 3 MINUTE
        |ON COMPLETION PRESERVE
        |ENABLE
        |DO
        |CODE;""".stripMargin.replaceAll("[\r\n]+", LINE_BREAK)

    assertResult(out) {
      statement.toString.replaceAll("[\r\n]+", LINE_BREAK)
    }

  }

  test("DropStatement prints correctly.") {

    val statement = new DropStatement("tableName", DropTypes.TABLE)

    val out = "DROP TABLE IF EXISTS `tableName`;".replaceAll("[\r\n]+", LINE_BREAK)

    assertResult(out) {
      statement.toString.replaceAll("[\r\n]+", LINE_BREAK)
    }

  }

}
