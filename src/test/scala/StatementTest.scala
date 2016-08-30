import org.scalatest.FunSuite
import statement._

class StatementTest extends FunSuite {

  test("CreateTableStatement prints correctly.") {

    val statement = new CreateTableStatement("tableName", Map("col1" -> "varchar(40)", "col2" -> "int"), "col2")

    val out = "CREATE TABLE `tableName`\n(\n`col1` VARCHAR(40),\n`col2` INT,\nPRIMARY KEY (`col2`)\n);"

    assertResult(out) {
      statement.toString
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

    val out = "DELIMITER $\nCREATE TRIGGER `triggerName` AFTER INSERT ON `watchMe`\nFOR EACH ROW\nBEGIN\nSELECT *;\nEND $\nDELIMITER ;"


    assertResult(out) {
      statement.toString
    }

  }

  test("DeleteStatement prints correctly.") {

    val statement = new DeleteStatement("tableName", "ID = 1")

    val out = "DELETE FROM `tableName`\nWHERE ID = 1;"

    assertResult(out) {
      statement.toString
    }

  }

  test("DropStatement prints correctly.") {

    val statement = new DropStatement("tableName", DropTypes.TABLE)

    val out = "DROP TABLE IF EXISTS `tableName`;"

    assertResult(out) {
      statement.toString
    }

  }

}
